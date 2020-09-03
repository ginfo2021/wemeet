package com.wemeet.dating.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import com.wemeet.dating.config.security.JwtTokenHandler;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.EntityNotFoundException;
import com.wemeet.dating.exception.InvalidCredentialException;
import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.EmailVerification;
import com.wemeet.dating.model.entity.ForgotPassword;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserDevice;
import com.wemeet.dating.model.enums.AccountType;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.enums.TokenType;
import com.wemeet.dating.model.request.ChangePasswordRequest;
import com.wemeet.dating.model.request.ResetPasswordRequest;
import com.wemeet.dating.model.user.UserLogin;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.model.user.UserSignup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_ALPHABET;
import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_NUMBER_GENERATOR;

@Service
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenHandler tokenHandler;
    private final EmailVerificationService emailVerificationService;
    private final UserPreferenceService userPreferenceService;
    private final ForgotPasswordService forgotPasswordService;
    private final UserDeviceService userDeviceService;
    public static final char[] VERIFY_EMAIL_ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${forgot.password.token.expire.hour}")
    private long passwordExpiryInHour;

    @Autowired
    public AuthService(UserService userService, BCryptPasswordEncoder passwordEncoder, JwtTokenHandler tokenHandler,
                       EmailVerificationService emailVerificationService, UserPreferenceService userPreferenceService, ForgotPasswordService forgotPasswordService, UserDeviceService userDeviceService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenHandler = tokenHandler;
        this.emailVerificationService = emailVerificationService;
        this.userPreferenceService = userPreferenceService;
        this.forgotPasswordService = forgotPasswordService;
        this.userDeviceService = userDeviceService;
    }

    public UserResult login(UserLogin userLogin) throws InvalidCredentialException {
        UserResult userResult = new UserResult();
        User existingUser = null;

        existingUser = userService.findUserByEmail(userLogin.getEmail());

        if (existingUser == null) {
            throw new InvalidCredentialException();
        }

        if (!passwordEncoder.matches(userLogin.getPassword(), existingUser.getPassword())) {
            throw new InvalidCredentialException();
        }

        String accessToken = tokenHandler.createToken(existingUser, new ArrayList<>());
        TokenInfo tokenInfo = new TokenInfo(accessToken, TokenType.BEARER);

        if (StringUtils.hasText(userLogin.getDeviceId())) {
            UserDevice userDevice = new UserDevice(userLogin.getDeviceId(), existingUser);
            userDeviceService.saveUserDevice(userDevice);
        }

        userResult.setUser(existingUser);
        userResult.setTokenInfo(tokenInfo);

        return userResult;
    }

    @Transactional
    public UserResult signUp(UserSignup userSignup) throws Exception {
        UserResult userResult = new UserResult();
        User newUser = userService.findUserByEmail(userSignup.getEmail());
        if (newUser != null) {
            throw new DuplicateKeyException("This user has already signed up, go to login");
        }
        newUser = userService.createOrUpdateUser(buildUserFromSignUp(userSignup));

        String accessToken = tokenHandler.createToken(newUser, new ArrayList<>());
        TokenInfo tokenInfo = new TokenInfo(accessToken, TokenType.BEARER);

        EmailVerification emailVerification = new EmailVerification(userSignup.getEmail(),
                NanoIdUtils.randomNanoId(DEFAULT_NUMBER_GENERATOR, VERIFY_EMAIL_ALPHABET, 8), true);

        emailVerificationService.saveEmail(emailVerification);
        userPreferenceService.createBasePreferenceForUser(newUser);
        if (StringUtils.hasText(userSignup.getDeviceId())) {
            UserDevice userDevice = new UserDevice(userSignup.getDeviceId(), newUser);
            userDeviceService.saveUserDevice(userDevice);
        }
        userResult.setUser(newUser);
        userResult.setTokenInfo(tokenInfo);
        return userResult;
    }

    private User buildUserFromSignUp(UserSignup userSignup) {
        User newUser = new User();
        newUser.setFirstName(userSignup.getFirstName());
        newUser.setLastName(userSignup.getLastName());
        newUser.setEmail(userSignup.getEmail());
        newUser.setDateOfBirth(userSignup.getDateOfBirth());
        newUser.setPhone(userSignup.getPhone());
        newUser.setGender(userSignup.getGender());
        newUser.setActive(false);
        newUser.setPhoneVerified(false);
        newUser.setEmailVerified(false);
        if (userSignup.getPassword() != null) {
            newUser.setPassword(passwordEncoder.encode(userSignup.getPassword()));
        }
        newUser.setType(AccountType.FREE);

        return newUser;
    }


    public void verifyEmail(String token) throws BadRequestException {
        emailVerificationService.verifyEmail(token);
    }


    public ForgotPassword generatePasswordToken(String email) throws EntityNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User Email not Found");
        }
        ForgotPassword forgotPasswordEntity = new ForgotPassword(true,
                NanoIdUtils.randomNanoId(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, 15), user
                , LocalDateTime.now().plusHours(passwordExpiryInHour));
        forgotPasswordService.saveEntity(forgotPasswordEntity);

        return forgotPasswordEntity;
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

        if (!forgotPassword.getUser().getId().equals(user.getId())) {
            return false;
        }
        return true;
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


    public void changePassword(ChangePasswordRequest changePassword, User user) throws BadRequestException {
        if (user == null) {
            throw new BadRequestException("User does not exist");
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

    public void deleteUser(User user) {

        userService.deleteUser(user, DeleteType.SELF);
    }
}
