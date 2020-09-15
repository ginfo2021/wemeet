package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;

public interface SwipeRepository extends BaseRepository<Swipe, Long> {

    Swipe findBySwiperAndSwipee(User swiper, User swipee);
}
