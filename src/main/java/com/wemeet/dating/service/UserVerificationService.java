package com.wemeet.dating.service;

import com.wemeet.dating.model.user.UserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserVerificationService.class);

    @Autowired
    public UserVerificationService(UserService userService) {
        this.userService = userService;

    }

    public boolean isActiveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        if (authentication.getPrincipal() instanceof UserResult) {
            UserResult userResult = (UserResult) authentication.getPrincipal();

            if (userResult == null || userResult.getUser() == null) {
                return false;
            }
            return userResult.getUser().isActive();

        } else {
            return false;
        }


    }
}
