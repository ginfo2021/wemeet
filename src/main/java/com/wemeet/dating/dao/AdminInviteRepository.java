package com.wemeet.dating.dao;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.wemeet.dating.model.entity.AdminInvite;

public interface AdminInviteRepository extends BaseRepository<AdminInvite, Long> {

    AdminInvite findByToken(String token);

    AdminInvite findTop1ByUserEmailOrderByIdDesc(String email);


}
