package com.wemeet.dating.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import com.wemeet.dating.config.security.JwtTokenHandler;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidCredentialException;
import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.EmailVerification;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.AccountType;
import com.wemeet.dating.model.enums.TokenType;
import com.wemeet.dating.model.user.UserLogin;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.model.user.UserSignup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public static final char[] VERIFY_EMAIL_ALPHABET =
            "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(UserService userService, BCryptPasswordEncoder passwordEncoder, JwtTokenHandler tokenHandler,
                       EmailVerificationService emailVerificationService, UserPreferenceService userPreferenceService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenHandler = tokenHandler;
        this.emailVerificationService = emailVerificationService;
        this.userPreferenceService = userPreferenceService;
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
}
