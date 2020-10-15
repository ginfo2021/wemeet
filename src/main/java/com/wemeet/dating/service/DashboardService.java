package com.wemeet.dating.service;

import com.wemeet.dating.model.response.DashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UserService userService;

    public DashboardResponse getDashBoardCount(){
        return DashboardResponse.builder()
                .usersCount(getTotalUsers())
                .deactivatedUsersCount(getTotalDeactivatedUsers())
                .freeAccounts(getTotalFreeAccounts())
                .premiumAccounts(getTotalPremiumAccounts())
                .build();
    }

    private long getTotalUsers(){
        return this.userService.getUsersCount();
    }

    private long getTotalDeactivatedUsers(){
        return this.userService.getTotalDeactivatedUsersCount();
    }

    private long getTotalFreeAccounts(){
        return this.userService.getTotalFreeAccountsCount();
    }

    private long getTotalPremiumAccounts(){
        return this.userService.getTotalPremiumAccountsCount();
    }
}
