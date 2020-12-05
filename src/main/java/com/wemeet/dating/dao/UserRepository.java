package com.wemeet.dating.dao;


import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User, Long> {

    User findByEmailAndDeletedIsFalse(String email);

    User findTop1ByEmailOrderByIdDesc(String email);

    Page<User> findBySuspendedIsTrueAndDeletedIsFalse(Pageable pageable);

    @Query(value = "select count(*) from user where deleted = true", nativeQuery = true)
    long countDeleted();

    @Query(value = "select count(*) from user where type = :accountType  and deleted = false", nativeQuery = true)
    long countByType(String accountType);

    @Query(value = "select count(*) from user where gender = 'MALE' and deleted = false", nativeQuery = true)
    long getMaleUsersCount();

    @Query(value = "select count(*) from user where gender = 'FEMALE' and deleted = false", nativeQuery = true)
    long getFemaleUsersCount();

    @Query(value = "select * from user where deleted = false order by date_created desc", nativeQuery = true)
    Page<User> getAllUsers(Pageable pageable);

    @Query(value = "select * from user where first_name like :name or last_name like :name and deleted = false order by date_created desc", nativeQuery = true)
    Page<User> getAllUsersSearch(String name, Pageable pageable);

    @Query(value = "select count(*) from user where deleted = false", nativeQuery = true)
    long countByDeletedFalse();

    @Query(value = "select count(*) from report where user_id = :userId", nativeQuery = true)
    long countUserReports(long userId);
}
