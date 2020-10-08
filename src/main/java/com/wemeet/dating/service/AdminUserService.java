package com.wemeet.dating.service;


import com.wemeet.dating.dao.AdminUserRepository;
import com.wemeet.dating.model.entity.AdminUser;
import com.wemeet.dating.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AdminUserRepository adminUserRepository;

    @Autowired
    public AdminUserService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }


    public AdminUser findUserByEmail(String email) {
        AdminUser adminUser = adminUserRepository.findByEmailAndDeletedIsFalse(email);
        return adminUser;
    }

    public AdminUser createOrUpdateUser(AdminUser adminUser) {
        return adminUserRepository.save(adminUser);
    }


    public AdminUser findById(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id).orElse(null);
        if (adminUser != null && adminUser.isDeleted()) {
            return null;
        }
        return adminUser;
    }

    public List<AdminUser> findAll() {
        List<AdminUser> adminUsers = new ArrayList<>();
        adminUserRepository.findAll().forEach(adminUsers::add);
        //remove deleted adminUsers
        adminUsers = adminUsers.stream()
                .filter(adminUser -> !adminUser.isDeleted())
                .collect(Collectors.toList());
        return adminUsers;

    }


}
