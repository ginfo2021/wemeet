package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.SwipeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface SwipeRepository extends BaseRepository<Swipe, Long> {

    Swipe findBySwiperAndSwipee(User swiper, User swipee);

    @Query(value = "SELECT A.swipee_id AS id FROM swipe A " +
            " INNER JOIN swipe B " +
            " ON A.swiper_id = B.swipee_id " +
            " WHERE A.swiper_id = B.swipee_id " +
            " AND B.swiper_id = A.swipee_id " +
            " AND  A.swiper_id = ?1 AND A.TYPE = ?2",
            countQuery = "SELECT COUNT(*) FROM( SELECT A.swipee_id AS id FROM swipe A " +
                    " INNER JOIN swipe B " +
                    " ON A.swiper_id = B.swipee_id " +
                    " WHERE A.swiper_id = B.swipee_id " +
                    " AND B.swiper_id = A.swipee_id " +
                    " AND  A.swiper_id = ?1 AND A.TYPE = ?2 ) t",
            nativeQuery = true)
    Page<BigInteger> findSwipersByType(Long swiperId, String swipeType, Pageable pageable);
}
