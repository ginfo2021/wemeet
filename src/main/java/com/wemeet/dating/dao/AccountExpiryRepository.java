package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.AccountExpiry;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountExpiryRepository extends BaseRepository<AccountExpiry, Long> {

    List<AccountExpiry> findByExpiredIsFalseAndExpiresAtBefore(LocalDateTime dayStart);
}
