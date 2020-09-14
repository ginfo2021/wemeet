package com.wemeet.dating.service;

import com.wemeet.dating.dao.UserImageRepository;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserImageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserImageRepository userImageRepository;

    @Autowired
    public UserImageService(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    public UserImage saveUserImage(UserImage userImage) {
        try {
            userImage = userImageRepository.save(userImage);
        } catch (DataIntegrityViolationException ex) {
            logger.error("User Image already exists", ex);
        }

        return userImage;

    }

    public List<UserImage> findImageByUser(User user) {
        return userImageRepository.findByUser(user);
    }

    public List<UserImage> findTop5ByUser(User user) {
        return userImageRepository.findTop5ByUserOrderByIdDesc(user);
    }
    
    
}
