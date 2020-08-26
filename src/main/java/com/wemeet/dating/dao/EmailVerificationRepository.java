package com.wemeet.dating.dao;


import com.wemeet.dating.model.entity.EmailVerification;

public interface EmailVerificationRepository extends BaseRepository<EmailVerification, Long> {

    EmailVerification findByToken(String token);
}
