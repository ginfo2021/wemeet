package com.wemeet.dating.service;


import com.wemeet.dating.dao.EmailVerificationRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.model.entity.EmailVerification;
import com.wemeet.dating.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(EmailVerification.class);

    @Autowired
    public EmailVerificationService(EmailVerificationRepository emailVerificationRepository, UserService userService) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userService = userService;
    }

    public EmailVerification saveEmail(EmailVerification emailVerification) {
        EmailVerification createdEmailVerification = emailVerificationRepository.save(emailVerification);
        return createdEmailVerification;
    }

    public void verifyEmail(String token) throws BadRequestException {
        EmailVerification emailVerification = emailVerificationRepository.findByToken(token);
        if (emailVerification == null) {
            throw new BadRequestException("Invalid verification token");
        }
        if (!(emailVerification.isActive())) {
            throw new BadRequestException("Invalid verification token");
        }
        emailVerification.setActive(false);
        emailVerificationRepository.save(emailVerification);

        User user = userService.findUserByEmail(emailVerification.getUserEmail());
        if (user != null && user.getId() != null && user.getId() > 0) {
            user.setActive(true);
            user.setEmailVerified(true);
            userService.createOrUpdateUser(user);
        }


    }
}
