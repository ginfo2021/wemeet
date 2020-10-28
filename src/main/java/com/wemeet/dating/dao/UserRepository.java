package com.wemeet.dating.dao;


import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmailAndDeletedIsFalse(String email);

    Page<User> findBySuspendedIsTrueAndDeletedIsFalse(Pageable pageable);

    @Query(countQuery = "select count(*) from user where active = false", nativeQuery = true)
    long countByActiveFalse();

    @Query(countQuery = "select count(*) from user where type = %?1%", nativeQuery = true)
    long countByType(AccountType accountType);

    @Query(value = "select count(*) from user where gender = 'MALE'", nativeQuery = true)
    long getMaleUsersCount();

    @Query(value = "select count(*) from user where gender = 'FEMALE'", nativeQuery = true)
    long getFemaleUsersCount();

    @Query(value = "select * from user where deleted = false order by date_created desc", nativeQuery = true)
    Page<User> getAllUsers(Pageable pageable);

    @Query(value = "select * from user where first_name like %?1% or last_name like %?1% order by date_created desc", nativeQuery = true)
    Page<User> getAllUsersSearch(String name, Pageable pageable);

    @Query(countQuery = "select count(*) from user where deleted=false", nativeQuery = true)
    long countByDeletedFalse();
}
