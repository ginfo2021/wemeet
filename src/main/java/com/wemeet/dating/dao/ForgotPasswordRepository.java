package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.ForgotPassword;
import com.wemeet.dating.model.entity.User;

public interface ForgotPasswordRepository extends BaseRepository<ForgotPassword, Long> {
    ForgotPassword findByToken(String token);

    ForgotPassword findTop1ByUserOrderByIdDesc(User user);
}
