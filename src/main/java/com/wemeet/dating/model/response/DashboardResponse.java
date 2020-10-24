package com.wemeet.dating.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    public long usersCount;
    public long deactivatedUsersCount;
    public long freeAccounts;
    public long premiumAccounts;
    public long maleUsersCount;
    public long femaleUsersCount;
}
