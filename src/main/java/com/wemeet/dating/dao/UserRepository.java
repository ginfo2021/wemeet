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

    @Query(value = "select count(*) from user where gender = 'MALE'", nativeQuery = true)
    long getTotalMaleUsersCount();

    @Query(value = "select count(*) from user where gender = 'FEMALE'", nativeQuery = true)
    long getTotalFemaleUsersCount();

    @Query(value = "select * from user order by date_created desc", nativeQuery = true)
    Page<User> getAllUsers(Pageable pageable);

    @Query(value = "select * from user where first_name like %?1% or last_name like %?1% order by date_created desc", nativeQuery = true)
    Page<User> getAllUsersSearch(String name, Pageable pageable);

}
