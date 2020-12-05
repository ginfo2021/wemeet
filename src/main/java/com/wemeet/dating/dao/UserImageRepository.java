package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserImage;

import java.util.List;

public interface UserImageRepository extends BaseRepository<UserImage, Long> {
    List<UserImage> findByUser(User user);

    List<UserImage> findTop5ByUserOrderByIdDesc(User user);

}
