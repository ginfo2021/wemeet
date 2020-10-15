package com.wemeet.dating.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.wemeet.dating.dao.AdminInviteRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.model.entity.AdminInvite;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.user.AdminSignup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminInviteService {

    private final AdminInviteRepository adminInviteRepository;

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AdminInviteService(AdminInviteRepository adminInviteRepository, UserService userService) {
        this.adminInviteRepository = adminInviteRepository;
        this.userService = userService;
    }


    public AdminInvite saveInvite(AdminInvite adminInvite) {
        AdminInvite createdAdminInvite = adminInviteRepository.save(adminInvite);
        return createdAdminInvite;
    }

    public boolean verifyInvite(AdminSignup adminSignup) throws BadRequestException {
        AdminInvite adminInvite = adminInviteRepository.findByToken(adminSignup.getToken());
        if (adminInvite == null) {
            return false;
        }
        if (!(adminInvite.isActive())) {
            return false;
        }

        if (!adminInvite.getUserEmail().equalsIgnoreCase(adminSignup.getEmail())) {
            return false;
        }
        return true;


    }

    public AdminInvite getByEmail(String email) {
        return adminInviteRepository.findTop1ByUserEmailOrderByIdDesc(email);
    }

    public AdminInvite getByToken(String token) {
        return adminInviteRepository.findByToken(token);
    }


}
