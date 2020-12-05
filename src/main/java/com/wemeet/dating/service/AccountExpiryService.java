package com.wemeet.dating.service;

import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.dao.AccountExpiryRepository;
import com.wemeet.dating.dao.PlanRepository;
import com.wemeet.dating.dao.SubscriptionRepository;
import com.wemeet.dating.model.entity.AccountExpiry;
import com.wemeet.dating.model.entity.Plan;
import com.wemeet.dating.model.entity.Subscription;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class AccountExpiryService {

    private final AccountExpiryRepository accountExpiryRepository;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final WemeetConfig config;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AccountExpiryService(AccountExpiryRepository accountExpiryRepository, UserService userService, SubscriptionRepository subscriptionRepository, PlanRepository planRepository, WemeetConfig config) {
        this.accountExpiryRepository = accountExpiryRepository;
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.config = config;
    }


    public void createFutureExpiry(AccountExpiry accountExpiry) {
        accountExpiryRepository.save(accountExpiry);
    }

    @Scheduled(cron = "${wemeet.cron.default.expression}")
    @Async
    public void getDailyExpiredUsersAndDowngrade() {
        List<AccountExpiry> accountsToExpiry = accountExpiryRepository.findByExpiredIsFalseAndExpiresAtBefore(DateUtil.getLocalStartofDay(new Date()));
        accountsToExpiry.forEach(this::downgradeUser);

    }

    @Transactional
    private void downgradeUser(AccountExpiry accountExpiry) {
        try {
            User user = userService.findById(accountExpiry.getUser().getId());
            Subscription subscription = subscriptionRepository.findByUser(user);

            downgradeUserToFree(user);
            userService.createOrUpdateUser(user);

            accountExpiry.setExpired(true);
            accountExpiryRepository.save(accountExpiry);

            subscription.setActive(false);
            subscriptionRepository.save(subscription);
        } catch (Exception ex) {
            logger.error("Unable to downgrade User", ex);
        }
    }

    public void downgradeUserToFree(User user) {
        if (user != null && user.getId() >= 0) {
            Plan defaultPlan = planRepository.findByCode(config.getWeMeetDefaultPlanCode());
            user.setType(defaultPlan.getName());
            userService.createOrUpdateUser(user);
        }

    }

}
