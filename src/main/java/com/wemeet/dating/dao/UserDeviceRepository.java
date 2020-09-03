package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserDevice;

import java.util.List;

public interface UserDeviceRepository extends BaseRepository<UserDevice, Long> {
    List<UserDevice> findByUser(User user);


}
