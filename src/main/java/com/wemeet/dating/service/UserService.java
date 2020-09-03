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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DeletedUserService deletedUserService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserService(UserRepository userRepository, DeletedUserService deletedUserService) {
        this.userRepository = userRepository;
        this.deletedUserService = deletedUserService;
    }

    public User findUserByEmail(String email) {
        User user = userRepository.findByEmailAndDeletedIsFalse(email);
        return user;
    }

    public User createOrUpdateUser(User user) {
        return userRepository.save(user);
    }


    public User findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && user.isDeleted()) {
            return null;
        }
        return user;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        //remove deleted users
        users = users.stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toList());
        return users;

    }


    @Transactional
    public void deleteUser(User user, DeleteType deleteType) {
        if (user == null)
            return;
        user.setDeleted(true);
        createOrUpdateUser(user);

        deletedUserService.createDeletedUser(new DeletedUser(user, user.getEmail(), deleteType, LocalDateTime.now()));
    }
}
