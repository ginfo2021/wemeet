package com.wemeet.dating.service;


import com.wemeet.dating.dao.UserRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.DeletedUser;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserImage;
import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.request.UserImageRequest;
import com.wemeet.dating.model.request.UserLocationRequest;
import com.wemeet.dating.model.request.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserPreferenceService userPreferenceService;
    private final UserRepository userRepository;
    private final DeletedUserService deletedUserService;
    private final UserImageService userImageService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserService(UserPreferenceService userPreferenceService, UserRepository userRepository, DeletedUserService deletedUserService, UserImageService userImageService) {
        this.userPreferenceService = userPreferenceService;
        this.userRepository = userRepository;
        this.deletedUserService = deletedUserService;
        this.userImageService = userImageService;
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

        DeletedUser deletedUser = new DeletedUser();
        deletedUser.setUser(user);
        deletedUser.setUserEmail(user.getEmail());
        deletedUser.setDeletedOn(LocalDateTime.now());
        deletedUser.setDeleteType(deleteType);

        deletedUserService.createDeletedUser(deletedUser);
    }


    public UserProfile getProfile(Long userId) throws Exception {
        return getProfile(findById(userId));
    }

    public UserProfile getProfile(User user) throws Exception {
        UserProfile userProfile = new UserProfile();
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        UserPreference userPreference = userPreferenceService.findUserPreference(user.getId());

        BeanUtils.copyProperties(user, userProfile);
        BeanUtils.copyProperties(userPreference, userProfile);
        userProfile.setAge(Period.between(user.getDateOfBirth().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), LocalDate.now()).getYears()
        );

        userProfile.setAdditionalImages(userImageService.findTop5ByUser(user)
                .stream()
                .map(UserImage::getImageUrl)
                .collect(Collectors.toList()));

        return userProfile;
    }

    @Transactional
    public UserProfile updateUserProfile(UserProfile userProfile, User user) throws Exception {

        createOrUpdateUser(buildUserFromProfile(userProfile));
        userPreferenceService.createOrUpdatePreference(buildPreferenceFromProfile(userProfile));


        return getProfile(user);
    }


    private User buildUserFromProfile(UserProfile userProfile) throws Exception {
        User user = findById(userProfile.getId());
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }


        if (userProfile.getGender() != null) {
            user.setGender(userProfile.getGender());
        }


        return user;
    }


    private UserPreference buildPreferenceFromProfile(UserProfile userProfile) throws BadRequestException {
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

        if (userProfile.getMaxAge() != null && userProfile.getMinAge() != null && userProfile.getMaxAge() < userProfile.getMinAge()) {
            throw new BadRequestException(" max age must be greater than min age");
        }


        if (userProfile.getSwipeRadius() != null) {
            userPreference.setSwipeRadius(userProfile.getSwipeRadius());
        }

        if (userProfile.getWorkStatus() != null) {
            userPreference.setWorkStatus(userProfile.getWorkStatus());
        }

        return userPreference;
    }

    public void updateUserLocation(UserLocationRequest locationRequest, User user) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        UserPreference userPreference = userPreferenceService.findUserPreference(user.getId());

        if (locationRequest.getLongitude() != null) {
            userPreference.setLongitude(locationRequest.getLongitude());
        }

        if (locationRequest.getLatitude() != null) {
            userPreference.setLatitude(locationRequest.getLatitude());
        }
        userPreferenceService.createOrUpdatePreference(userPreference);
    }

    public void updateUserImages(UserImageRequest imageRequest, User user) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        if (StringUtils.hasText(imageRequest.getProfileImage())) {
            user.setProfileImage(imageRequest.getProfileImage());
        }
        createOrUpdateUser(user);

        if (imageRequest.getAdditionalImages() != null && !imageRequest.getAdditionalImages().isEmpty()) {

            for (String otherImage : imageRequest.getAdditionalImages()) {
                if (StringUtils.hasText(otherImage)) {
                    UserImage userImage = new UserImage();
                    userImage.setUser(user);
                    userImage.setImageUrl(otherImage);
                    userImageService.saveUserImage(userImage);
                }
            }

        }
    }
}
