package com.wemeet.dating.dao;


import com.wemeet.dating.model.entity.User;

public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmail(String email);

}
