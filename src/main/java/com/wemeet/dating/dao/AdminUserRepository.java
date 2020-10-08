package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.AdminUser;

public interface AdminUserRepository extends BaseRepository<AdminUser, Long> {
    AdminUser findByEmailAndDeletedIsFalse(String email);
}
