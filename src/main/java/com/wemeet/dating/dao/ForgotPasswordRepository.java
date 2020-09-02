package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.ForgotPassword;

public interface ForgotPasswordRepository extends BaseRepository<ForgotPassword, Long> {
    ForgotPassword findByToken(String token);
}
