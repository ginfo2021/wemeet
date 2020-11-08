package com.wemeet.dating.service;

import com.wemeet.dating.dao.UserDeviceRepository;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDeviceService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserDeviceRepository userDeviceRepository;

    @Autowired
    public UserDeviceService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    public UserDevice saveUserDevice(UserDevice userDevice) {
        try {
            userDevice = userDeviceRepository.save(userDevice);
        } catch (DataIntegrityViolationException ex) {
            logger.error("User device already exists", ex);
        }

        return userDevice;

    }

    public void deleteDevice(UserDevice userDevice){
        userDeviceRepository.delete(userDevice);
    }

    public UserDevice findOne(String deviceId){
        return userDeviceRepository.findByDeviceId(deviceId);
    }

    public UserDevice findByDeviceAndUser(String deviceId, User user){
        return userDeviceRepository.findByDeviceIdAndUser(deviceId, user);
    }


    public List<UserDevice> findDeviceByUser(User user) {
        return userDeviceRepository.findByUser(user);
    }
}
