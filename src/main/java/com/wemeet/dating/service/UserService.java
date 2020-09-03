package com.wemeet.dating.service;


import com.wemeet.dating.dao.UserRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.model.entity.DeletedUser;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.request.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserPreferenceService userPreferenceService;
    private final UserRepository userRepository;
    private final DeletedUserService deletedUserService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserService(UserPreferenceService userPreferenceService, UserRepository userRepository, DeletedUserService deletedUserService) {
        this.userPreferenceService = userPreferenceService;
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

    public UserProfile getProfile(Long id) throws BadRequestException {
        UserProfile userProfile = new UserProfile();
        User user = findById(id);
        if (user == null || user.getId() <= 0) {
            throw new BadRequestException("User does Not exist");
        }
        UserPreference userPreference = userPreferenceService.findUserPreference(id);

        BeanUtils.copyProperties(user, userProfile);
        BeanUtils.copyProperties(userPreference, userProfile);

        return userProfile;
    }

    @Transactional
    public UserProfile updateUserProfile(UserProfile userProfile) throws BadRequestException {

        createOrUpdateUser(buildUserFromProfile(userProfile));
        userPreferenceService.createOrUpdatePreference(buildPreferenceFromProfile(userProfile));

        return getProfile(userProfile.getId());
    }


    private User buildUserFromProfile(UserProfile userProfile) throws BadRequestException {
        User user = findById(userProfile.getId());
        if (user == null || user.getId() <= 0) {
            throw new BadRequestException("User does Not exist");
        }


        if (userProfile.getGender() != null) {
            user.setGender(userProfile.getGender());
        }

        return user;
    }


    private UserPreference buildPreferenceFromProfile(UserProfile userProfile) {
        UserPreference userPreference = userPreferenceService.findUserPreference(userProfile.getId());
        if (StringUtils.hasText(userProfile.getBio())) {
            userPreference.setBio(userProfile.getBio());
        }

        if (userProfile.getGenderPreference() != null) {
            userPreference.setGenderPreference(userProfile.getGenderPreference());
        }

        if (userProfile.getMinAge() != null) {
            userPreference.setMinAge(userProfile.getMinAge());
        }

        if (userProfile.getMaxAge() != null) {
            userPreference.setMaxAge(userProfile.getMaxAge());
        }

        if (userProfile.getSwipeRadius() != null) {
            userPreference.setSwipeRadius(userProfile.getSwipeRadius());
        }

        if (userProfile.getWorkStatus() != null) {
            userPreference.setWorkStatus(userProfile.getWorkStatus());
        }

        return userPreference;
    }

}
