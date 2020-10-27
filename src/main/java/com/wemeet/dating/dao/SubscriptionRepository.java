package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Subscription;
import com.wemeet.dating.model.entity.User;

public interface SubscriptionRepository extends BaseRepository<Subscription, Long> {
    Subscription findByUser(User user);
}
