package com.wemeet.dating.service;


import com.wemeet.dating.dao.UserRepository;
import com.wemeet.dating.model.entity.DeletedUser;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.DeleteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DeletedUserService deletedUserService;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, DeletedUserService deletedUserService) {
        this.userRepository = userRepository;
        this.deletedUserService = deletedUserService;
    }

    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    public User createOrUpdateUser(User user) {
        return userRepository.save(user);
    }


    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteUser(User user, DeleteType deleteType) {
        user.setDeleted(true);
        createOrUpdateUser(user);

        deletedUserService.createDeletedUser(new DeletedUser(user, user.getEmail(), deleteType, LocalDateTime.now()));
    }
}
