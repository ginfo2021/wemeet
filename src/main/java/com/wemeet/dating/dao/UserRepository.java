package com.wemeet.dating.dao;


import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmailAndDeletedIsFalse(String email);

    Page<User> findBySuspendedIsTrueAndDeletedIsFalse(Pageable pageable);

    @Query(value = "select count(*) from user where active = false", nativeQuery = true)
    long getDeactivatedUsersCount();

    @Query(value = "select count(*) from user where type = 'FREE'", nativeQuery = true)
    long getFreeUsersCount();

    @Query(value = "select count(*) from user where type = 'PREMIUM'", nativeQuery = true)
    long getPremiumUsersCount();

}
