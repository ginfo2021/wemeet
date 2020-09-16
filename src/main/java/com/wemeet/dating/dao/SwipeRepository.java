package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.SwipeType;
import com.wemeet.dating.model.request.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface SwipeRepository extends BaseRepository<Swipe, Long> {

    Swipe findBySwiperAndSwipee(User swiper, User swipee);

    @Query(
            value
                    = "SELECT A.swipee_id AS id FROM swipe A " +
                    " INNER JOIN swipe B " +
                    " ON A.swiper_id = B.swipee_id " +
                    " WHERE A.swiper_id = B.swipee_id " +
                    " AND B.swiper_id = A.swipee_id " +
                    " AND  A.swiper_id = :userId AND A.TYPE = :swipeType",
            countQuery
                    = "SELECT COUNT(*) FROM( SELECT A.swipee_id AS id FROM swipe A " +
                    " INNER JOIN swipe B " +
                    " ON A.swiper_id = B.swipee_id " +
                    " WHERE A.swiper_id = B.swipee_id " +
                    " AND B.swiper_id = A.swipee_id " +
                    " AND  A.swiper_id = :userId AND A.TYPE = :swipeType ) t",
            nativeQuery = true)
    Page<BigInteger> findSwipersByType(Long userId, String swipeType, Pageable pageable
    );

    @Query(
            value
                    = "SELECT U.*, TIMESTAMPDIFF(YEAR, U.date_of_birth, CURDATE()) AS age ," +
                    "UP.bio, UP.gender_preference, UP.swipe_radius, UP.work_status, UP.min_age, UP.max_age " +
                    "FROM user U  " +
                    "INNER JOIN user_preference UP " +
                    "ON U.id = UP.id " +
                    "WHERE  " +
                    "U.deleted = 0  " +
                    "AND U.id <> :userId " +
                    "AND :userId NOT IN (SELECT swiper_id FROM swipe WHERE swipee_id = U.id) " +
                    "AND U.gender IN :genderPreferenceList " +
                    "HAVING age >= :minAge AND age <= :maxAge ",
            nativeQuery = true
)
    List<UserProfile> findSwipeSuggestions(Long userId, List<String> genderPreferenceList, int minAge, int maxAge);

    @Query(
            value
                    = "SELECT U.*" +
                    "FROM user U  " +
                    "INNER JOIN user_preference UP " +
                    "ON U.id = UP.id " +
                    "WHERE  " +
                    "U.deleted = 0  " +
                    "AND U.active = 1  " +
                    "AND U.suspended = 0  " +
                    "AND U.id <> :userId " +
                    "AND :userId NOT IN (SELECT swiper_id FROM swipe WHERE swipee_id = U.id) " +
                    "AND U.gender IN :genderPreferenceList " +
                    "LIMIT :limit",
            nativeQuery = true
    )
    List<BigInteger> findSwipeSuggestions(Long userId, List<String> genderPreferenceList, int limit);

}
