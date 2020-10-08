package com.wemeet.dating.dao;


import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;

public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmailAndDeletedIsFalse(String email);

    Page<User> findBySuspendedIsTrueAndDeletedIsFalse(Pageable pageable);

}
