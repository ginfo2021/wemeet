package com.wemeet.dating.config.security;

import com.wemeet.dating.model.entity.AdminUser;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.service.AdminUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupBean {

    @Value("${wemeet.admin.default.user}")
    private String defaultUser;
    @Value("${wemeet.admin.default.password}")
    private String defaultPassword;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AdminUserService adminUserService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {

        AdminUser adminUser = adminUserService.findUserByEmail(defaultUser);
        if (adminUser == null || adminUser.getId() <= 0) {
            adminUser = new AdminUser();
            adminUser.setActive(true);
            adminUser.setFirstName("Super");
            adminUser.setLastName("User");
            adminUser.setEmail(defaultUser);
            adminUser.setPassword(passwordEncoder.encode(defaultPassword));

            adminUser = adminUserService.createOrUpdateUser(adminUser);
            logger.info("Successfully created default Admin User");

        }


    }
}
