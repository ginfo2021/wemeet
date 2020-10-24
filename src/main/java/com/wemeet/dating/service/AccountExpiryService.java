package com.wemeet.dating.service;

import com.wemeet.dating.dao.AccountExpiryRepository;
import com.wemeet.dating.model.entity.AccountExpiry;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.AccountType;
import com.wemeet.dating.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class AccountExpiryService {

    private final AccountExpiryRepository accountExpiryRepository;
    private final UserService userService;

    @Autowired
    public AccountExpiryService(AccountExpiryRepository accountExpiryRepository, UserService userService) {
        this.accountExpiryRepository = accountExpiryRepository;
        this.userService = userService;
    }


    @Scheduled(cron = "${wemeet.cron.default.expression}")
    @Transactional
    @Async
    public void getDailyExpiredUsersAndDowngrade() {
        List<AccountExpiry> accountsToExpiry = accountExpiryRepository.findByExpiredIsFalseAndExpiresAtBefore(DateUtil.getLocalStartofDay(new Date()));
        accountsToExpiry.forEach(accountExpiry -> {
            User user = userService.findById(accountExpiry.getUser().getId());
            user.setType(AccountType.FREE);
            userService.createOrUpdateUser(user);
        });

    }

}
