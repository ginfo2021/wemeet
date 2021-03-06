package com.wemeet.dating.service;

import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.config.security.JwtTokenHandler;
import com.wemeet.dating.dao.PlanRepository;
import com.wemeet.dating.events.OnGeneratePasswordToken;
import com.wemeet.dating.events.OnInviteAdminEvent;
import com.wemeet.dating.events.OnRegistrationCompleteEvent;
import com.wemeet.dating.exception.*;
import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.*;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.enums.TokenType;
import com.wemeet.dating.model.enums.UserType;
import com.wemeet.dating.model.request.ChangePasswordRequest;
import com.wemeet.dating.model.request.ResetPasswordRequest;
import com.wemeet.dating.model.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Random;

@Service
public class AuthService {

    private final UserService userService;
    private final AdminUserService adminUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenHandler tokenHandler;
    private final EmailVerificationService emailVerificationService;
    private final UserPreferenceService userPreferenceService;
    private final ForgotPasswordService forgotPasswordService;
    private final UserDeviceService userDeviceService;
    private final AdminInviteService adminInviteService;
    private final PlanRepository planRepository;
    private final WemeetConfig config;

    @Value("${forgot.password.token.expire.hour}")
    private long passwordExpiryInHour;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthService(UserService userService, AdminUserService adminUserService, BCryptPasswordEncoder passwordEncoder, JwtTokenHandler tokenHandler,
                       EmailVerificationService emailVerificationService, UserPreferenceService userPreferenceService,
                       ForgotPasswordService forgotPasswordService, UserDeviceService userDeviceService,
                       NotificationService notificationService,
                       AdminInviteService adminInviteService, PlanRepository planRepository, WemeetConfig config) {
        this.userService = userService;
        this.adminUserService = adminUserService;
        this.passwordEncoder = passwordEncoder;
        this.tokenHandler = tokenHandler;
        this.emailVerificationService = emailVerificationService;
        this.userPreferenceService = userPreferenceService;
        this.forgotPasswordService = forgotPasswordService;
        this.userDeviceService = userDeviceService;
        this.adminInviteService = adminInviteService;
        this.planRepository = planRepository;
        this.config = config;
    }

    public void logout(UserLogout logout) throws Exception {
        try {
            if (StringUtils.hasText(logout.getDeviceId()) && StringUtils.hasText(logout.getUserEmail())) {
                User user = userService.findUserByEmail(logout.getUserEmail());
                userDeviceService.deleteDevice(userDeviceService.findByDeviceAndUser(logout.getDeviceId(), user));
                userPreferenceService.updateUserLocation(user, logout);

            }
        }catch (Exception ex){
            logger.error("Error deleting user device",ex);
        }



    }

    public UserResult login(UserLogin userLogin) throws InvalidCredentialException, SuspendedUserException {
       
        return null;
        User existingUser;

        existingUser = userService.findUserByEmail(userLogin.getEmail());

        if (existingUser == null) {
            throw new InvalidCredentialException();
        }

        if (!passwordEncoder.matches(userLogin.getPassword(), existingUser.getPassword())) {
            throw new InvalidCredentialException();
        }

        if(existingUser.isSuspended()){
            throw new SuspendedUserException();
        }

        String accessToken = tokenHandler.createToken(existingUser, null);

        if (StringUtils.hasText(userLogin.getDeviceId())) {
            UserDevice userDevice = new UserDevice();
            userDevice.setDeviceId(userLogin.getDeviceId());
            userDevice.setUser(existingUser);
            userDeviceService.saveUserDevice(userDevice);
        }
        userPreferenceService.updateUserLocation(existingUser, userLogin);

        return UserResult
                .builder()
                .tokenInfo(
                        TokenInfo
                                .builder()
                                .accessToken(accessToken)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(existingUser)
                .build();
    }


    public UserResult adminLogin(AdminLogin adminLogin) throws InvalidCredentialException {
        AdminUser adminUser;
        User user = new User();

        adminUser = adminUserService.findUserByEmail(adminLogin.getEmail());

        if (adminUser == null) {
            throw new InvalidCredentialException();
        }

        if (!passwordEncoder.matches(adminLogin.getPassword(), adminUser.getPassword())) {
            throw new InvalidCredentialException();
        }

        BeanUtils.copyProperties(adminUser, user);
        String accessToken = tokenHandler.createToken(user, UserType.ADMIN.name());

        return UserResult
                .builder()
                .tokenInfo(
                        TokenInfo
                                .builder()
                                .accessToken(accessToken)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(user)
                .build();
    }


    public void inviteAdmin(User user, String email) throws Exception {

        User existingUser = userService.findUserByEmail(email);
        if (existingUser != null) {
            throw new BadRequestException("Cannot use a user email for admin");
        }
        AdminUser adminUser = adminUserService.findUserByEmail(email);
        if (adminUser != null) {
            throw new DuplicateKeyException("This user is already an admin");
        }

        AdminInvite existingInvite = adminInviteService.getByEmail(email);
        if (existingInvite != null && existingInvite.isActive()) {
            throw new DuplicateKeyException("This user already has an active invite");
        }


        AdminInvite adminInvite = new AdminInvite();
        adminInvite.setUserEmail(email);
        adminInvite.setToken(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        adminInvite.setActive(true);

        adminInvite = adminInviteService.saveInvite(adminInvite);
        eventPublisher.publishEvent(new OnInviteAdminEvent(adminInvite));
    }


    @Transactional
    public UserResult adminSignUp(AdminSignup adminSignup) throws Exception {

        if (!adminInviteService.verifyInvite(adminSignup)) {
            throw new BadRequestException("Invalid Token");
        }
        User user = new User();

        AdminUser adminUser = adminUserService.createOrUpdateUser(buildUserFromSignUp(adminSignup));


        AdminInvite adminInvite = adminInviteService.getByToken(adminSignup.getToken());
        adminInvite.setActive(false);
        adminInviteService.saveInvite(adminInvite);


        BeanUtils.copyProperties(adminUser, user);
        String accessToken = tokenHandler.createToken(user, UserType.ADMIN.name());

        return UserResult
                .builder()
                .tokenInfo(
                        TokenInfo
                                .builder()
                                .accessToken(accessToken)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(user)
                .build();
    }


    @Transactional
    public UserResult signUp1(UserSignup userSignup) throws Exception {
        User newUser = userService.findUserByEmail(userSignup.getEmail());
        if (newUser != null) {
            throw new DuplicateKeyException("This user has already signed up, go to login");
        }
        newUser = userService.createOrUpdateUser(buildUserFromSignUp(userSignup));

        String accessToken = tokenHandler.createToken(newUser, null);

        EmailVerification emailVerification = new EmailVerification();

        emailVerification.setUserEmail(userSignup.getEmail());

        emailVerification.setToken(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        emailVerification.setActive(true);

        emailVerification = emailVerificationService.saveEmail(emailVerification);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(emailVerification));

        userPreferenceService.createBasePreferenceForUser(newUser, userSignup);
        if (StringUtils.hasText(userSignup.getDeviceId())) {
            UserDevice userDevice = new UserDevice();
            userDevice.setDeviceId(userSignup.getDeviceId());
            userDevice.setUser(newUser);
            userDeviceService.saveUserDevice(userDevice);
        }

        return UserResult.builder()
                .tokenInfo(
                        TokenInfo
                                .builder()
                                .accessToken(accessToken)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(newUser)
                .build();
    }

    @Transactional
    public UserResult socialSignUp(UserSignup userSignup) throws Exception {
        User newUser = userService.findUserByEmail(userSignup.getEmail());
        if (newUser != null) {
            throw new DuplicateKeyException("This user has already signed up, go to login");
        }
        newUser = userService.createOrUpdateUser(buildUserFromSocialSignUp(userSignup));

        String accessToken = tokenHandler.createToken(newUser, null);

        EmailVerification emailVerification = new EmailVerification();

        emailVerification.setUserEmail(userSignup.getEmail());

        emailVerification.setToken(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        emailVerification.setActive(true);

        emailVerification = emailVerificationService.saveEmail(emailVerification);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(emailVerification));

        userPreferenceService.createBasePreferenceForUser(newUser, userSignup);
        if (StringUtils.hasText(userSignup.getDeviceId())) {
            UserDevice userDevice = new UserDevice();
            userDevice.setDeviceId(userSignup.getDeviceId());
            userDevice.setUser(newUser);
            userDeviceService.saveUserDevice(userDevice);
        }

        return UserResult.builder()
                .tokenInfo(
                        TokenInfo
                                .builder()
                                .accessToken(accessToken)
                                .tokenType(TokenType.BEARER)
                                .build()
                )
                .user(newUser)
                .build();
    }
    private AdminUser buildUserFromSignUp(AdminSignup signup) throws BadRequestException {
        AdminUser newUser = new AdminUser();
        newUser.setFirstName(signup.getFirstName());
        newUser.setLastName(signup.getLastName());
        newUser.setEmail(signup.getEmail());
        newUser.setActive(true);
        if (signup.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(signup.getPassword()));
        }

        return newUser;
    }

    private User buildUserFromSignUp(UserSignup userSignup) throws BadRequestException {
        if (Period.between(userSignup.getDateOfBirth().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), LocalDate.now()).getYears() < 18) {
            throw new BadRequestException("You must Be 18 years to join this platform");
        }

        User newUser = new User();
        newUser.setFirstName(userSignup.getFirstName());
        newUser.setLastName(userSignup.getLastName());
        newUser.setUserName(userSignup.getUserName());
        newUser.setEmail(userSignup.getEmail());
        newUser.setPhone(userSignup.getPhone());
        newUser.setGender(userSignup.getGender());
        newUser.setActive(true);
        newUser.setPhoneVerified(false);
        newUser.setEmailVerified(false);
        if (userSignup.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(userSignup.getPassword()));
        }
        newUser.setDateOfBirth(userSignup.getDateOfBirth());
        Plan defaultPlan = planRepository.findByCode(config.getWeMeetDefaultPlanCode());
        newUser.setType(defaultPlan.getName());

        return newUser;
    }

    private User buildUserFromSocialSignUp(UserSignup userSignup) throws BadRequestException {
        if (Period.between(userSignup.getDateOfBirth().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), LocalDate.now()).getYears() < 18) {
            throw new BadRequestException("You must Be 18 years to join this platform");
        }

        User newUser = new User();
        newUser.setFirstName(userSignup.getFirstName());
        newUser.setLastName(userSignup.getLastName());
        newUser.setUserName(userSignup.getUserName());
        newUser.setEmail(userSignup.getEmail());
        newUser.setPhone(userSignup.getPhone());
        newUser.setGender(userSignup.getGender());
        newUser.setActive(false);
        newUser.setPhoneVerified(false);
        newUser.setEmailVerified(false);
        if (userSignup.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(userSignup.getPassword()));
        }
        newUser.setDateOfBirth(userSignup.getDateOfBirth());
        Plan defaultPlan = planRepository.findByCode(config.getWeMeetDefaultPlanCode());
        newUser.setType(defaultPlan.getName());

        return newUser;
    }

    public void verifyEmail(String token) throws BadRequestException {
        emailVerificationService.verifyEmail(token);
    }


    public void generatePasswordToken(String email) throws EntityNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User Email not Found");
        }
        ForgotPassword forgotPasswordEntity = new ForgotPassword();
        forgotPasswordEntity.setActive(true);
        forgotPasswordEntity.setToken(new DecimalFormat("000000").format(new Random().nextInt(999999)));
        forgotPasswordEntity.setUser(user);
        forgotPasswordEntity.setExpiresAt(LocalDateTime.now().plusHours(passwordExpiryInHour));

        forgotPasswordService.saveEntity(forgotPasswordEntity);
        eventPublisher.publishEvent(new OnGeneratePasswordToken(forgotPasswordEntity));


    }

    public boolean verifyForgotPasswordToken(String token, String email) {
        ForgotPassword forgotPassword = forgotPasswordService.findByToken(token);
        if (forgotPassword == null) {
            return false;
        }
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return false;
        }
        if (!forgotPassword.isActive()) {
            return false;
        }

        if (LocalDateTime.now().isAfter(forgotPassword.getExpiresAt())) {
            return false;
        }

        return forgotPassword.getUser().getId().equals(user.getId());
    }

    public void resetPassword(ResetPasswordRequest resetPassword) throws BadRequestException {

        if (verifyForgotPasswordToken(resetPassword.getToken(), resetPassword.getEmail())) {
            User user = userService.findUserByEmail(resetPassword.getEmail());
            if (!(resetPassword.getConfirmPassword().contentEquals(resetPassword.getPassword()))) {
                throw new BadRequestException("Passwords do not match");
            }
            if (passwordEncoder.matches(resetPassword.getPassword(), user.getPassword())) {
                throw new BadRequestException("Duplicate password. Please enter a new Password");
            }

            user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
            userService.createOrUpdateUser(user);

            ForgotPassword forgotPassword = forgotPasswordService.findByToken(resetPassword.getToken());
            forgotPassword.setActive(false);
            forgotPasswordService.saveEntity(forgotPassword);
        } else {
            throw new BadRequestException("Invalid Email or Token");
        }
    }


    public void changePassword(ChangePasswordRequest changePassword, User user) throws BadRequestException, InvalidJwtAuthenticationException {
        if (user == null) {
            throw new InvalidJwtAuthenticationException("User with token does not exist");
        }


        if (!(changePassword.getConfirmPassword().contentEquals(changePassword.getNewPassword()))) {
            throw new BadRequestException("Passwords do not match");
        }

        user = userService.findById(user.getId());
        if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect Password");
        }

        if (passwordEncoder.matches(changePassword.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New Password cannot be the same as old Password");
        }

        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userService.createOrUpdateUser(user);

    }

    public void deleteUser(User user) throws Exception {
        if (user == null) {
            throw new InvalidJwtAuthenticationException("User with token not Found");
        }
        userService.deleteUser(user, DeleteType.SELF);
    }

    public EmailVerification getEmailVerification(User user) throws Exception {
        if (user == null) {
            throw new InvalidJwtAuthenticationException("User with token not Found");
        }

        return emailVerificationService.getByEmail(user.getEmail());
    }

    public void resendActivationEmail(User user) throws Exception {
        if (user == null) {
            throw new InvalidJwtAuthenticationException("User with token not Found");
        }
        EmailVerification emailVerification = emailVerificationService.getByEmail(user.getEmail());

        if (emailVerification == null || !emailVerification.isActive()) {
            emailVerification = new EmailVerification();
            emailVerification.setUserEmail(user.getEmail());
            emailVerification.setToken(new DecimalFormat("000000").format(new Random().nextInt(999999)));
            emailVerification.setActive(true);
            emailVerification = emailVerificationService.saveEmail(emailVerification);
        }
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(emailVerification));
    }


    public ForgotPassword getForgotPasswordToken(User user) throws Exception {
        if (user == null) {
            throw new InvalidJwtAuthenticationException("User with token not Found");
        }

        return forgotPasswordService.getByUser(user);
    }
}
