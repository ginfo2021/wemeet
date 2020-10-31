package com.wemeet.dating.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.dao.FeatureLimitRepository;
import com.wemeet.dating.dao.PlanRepository;
import com.wemeet.dating.dao.SubscriptionRepository;
import com.wemeet.dating.dao.UserRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.exception.UserNotPremiumException;
import com.wemeet.dating.model.entity.*;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.request.*;
import com.wemeet.dating.model.response.DisableResponse;
import com.wemeet.dating.model.response.PageResponse;
import com.wemeet.dating.model.response.PaystackSubscriptionResponse;
import com.wemeet.dating.model.response.PlanWithLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final WemeetConfig config;
    private final FeatureLimitRepository limitRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaystackService paystackService;
    private final FeatureLimitService featureLimitService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public UserService(UserPreferenceService userPreferenceService, UserRepository userRepository, DeletedUserService deletedUserService, UserImageService userImageService, WemeetConfig config, FeatureLimitRepository limitRepository, PlanRepository planRepository, SubscriptionRepository subscriptionRepository, PaystackService paystackService, FeatureLimitService featureLimitService) {
        this.userPreferenceService = userPreferenceService;
        this.userRepository = userRepository;
        this.deletedUserService = deletedUserService;
        this.userImageService = userImageService;
        this.config = config;
        this.limitRepository = limitRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paystackService = paystackService;
        this.featureLimitService = featureLimitService;
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

        disableUserSubcription(user);

    }

    private void disableUserSubcription(User user) {
        try {


            Subscription subscription = subscriptionRepository.findByUser(user);
            if (subscription != null && subscription.isActive()) {

                PaymentWebhookRequest subscriptionResponse = paystackService.getSubscription(subscription.getSubscription_code(), PaymentWebhookRequest.class);
                PaystackSubscriptionResponse paystackSubscriptionResponse = objectMapper.convertValue(subscriptionResponse.getData(), PaystackSubscriptionResponse.class);

                if (paystackSubscriptionResponse.getStatus().equals("active")) {
                    DisableRequest disableRequest = new DisableRequest();
                    disableRequest.setCode(paystackSubscriptionResponse.getSubscription_code());
                    disableRequest.setToken(paystackSubscriptionResponse.getEmail_token());
                    DisableResponse paymentResponse = paystackService.disableSubscription(disableRequest, DisableResponse.class);
                }

            }
        } catch (Exception ex) {
            logger.error("Unable to disable paystack subscription for user", ex);
        }
    }


    public UserProfile getProfile(Long userId) throws Exception {
        return getProfile(findById(userId));
    }
    public UserProfileBig getProfileBig(User user) throws Exception {
        UserProfileBig profileBig = new UserProfileBig();
        BeanUtils.copyProperties(getProfile(user), profileBig);
        profileBig.setReportCount(userRepository.countUserReports(user.getId()));
        return profileBig;
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

        if (userProfile.getHideLocation() != null) {
            userPreference.setHideLocation(userProfile.getHideLocation());
        }

        if (userProfile.getHideProfile() != null) {
            userPreference.setHideProfile(userProfile.getHideProfile());
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

        validateUserTypeLocationLimit(user);

        UserPreference userPreference = userPreferenceService.findUserPreference(user.getId());

        if (locationRequest.getLongitude() != null) {
            userPreference.setLongitude(locationRequest.getLongitude());
        }

        if (locationRequest.getLatitude() != null) {
            userPreference.setLatitude(locationRequest.getLatitude());
        }
        userPreferenceService.createOrUpdatePreference(userPreference);
    }

    private void validateUserTypeLocationLimit(User user) throws UserNotPremiumException {

        Plan plan = planRepository.findByName(user.getType());
        FeatureLimit featureLimit = limitRepository.findByPlan(plan);

        if (featureLimit != null) {
            if (!featureLimit.isUpdateLocation()) {
                throw new UserNotPremiumException("You are not allowed to update Location");
            }
        } else {
            if (!config.isWemeetDefaultUpdateLocation()) {
                throw new UserNotPremiumException("You are not allowed to update Location");
            }
        }

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

    public void suspendUser(User user) {
        if (user == null)
            return;
        user.setSuspended(true);
        createOrUpdateUser(user);
        disableUserSubcription(user);
    }

    public void restoreUser(User user) {
        if (user == null)
            return;
        user.setSuspended(false);
        createOrUpdateUser(user);
    }


    public PageResponse<UserProfile> getsuspendedUsers(int pageNum, int pageSize) {

        List<UserProfile> userProfiles = new ArrayList<>();
        PageResponse<UserProfile> userProfilePage = new PageResponse<>();
        Page<User> userList = userRepository.findBySuspendedIsTrueAndDeletedIsFalse(PageRequest.of(pageNum, pageSize));


        userList.toList().forEach(a -> {
            try {
                userProfiles.add(getProfile(a.getId()));
            } catch (Exception e) {
                logger.error("Error fetching user profile for user id: " + a, e);
            }
        });

        userProfilePage.setContent(userProfiles);
        userProfilePage.setPageNum(userList.getNumber());
        userProfilePage.setPageSize(userList.getSize());
        userProfilePage.setNumberOfElements(userList.getNumberOfElements());
        userProfilePage.setTotalElements(userList.getTotalElements());
        userProfilePage.setTotalPages(userList.getTotalPages());

        return userProfilePage;

    }

    @Transactional(readOnly = true)
    public long getUsersCount() {
        return userRepository.countByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public long getTotalDeactivatedUsersCount() {
        return userRepository.countDeleted();
    }

    @Transactional(readOnly = true)
    public long getTotalFreeAccountsCount() {
        return userRepository.countByType("FREE");
    }

    @Transactional(readOnly = true)
    public long getTotalPremiumAccountsCount() {
        return userRepository.countByType("PREMIUM");
    }

    @Transactional(readOnly = true)
    public PageResponse<UserProfileBig> getAllUsers(String name, int pageNum, int pageSize) {
        Page<User> userPage = null;
        List<UserProfileBig> userProfiles = new ArrayList<>();
        PageResponse<UserProfileBig> userProfilePage = new PageResponse<>();
        if (name != null) {
            userPage = userRepository.getAllUsersSearch(name, PageRequest.of(pageNum, pageSize));
        } else {
            userPage = userRepository.getAllUsers(PageRequest.of(pageNum, pageSize));
        }
        userPage.toList().forEach(a -> {
            try {
                userProfiles.add(getProfileBig(a));
            } catch (Exception e) {
                logger.error("Error fetching user profile for user id: " + a, e);
            }
        });

        userProfilePage.setContent(userProfiles);
        userProfilePage.setPageNum(userPage.getNumber());
        userProfilePage.setPageSize(userPage.getSize());
        userProfilePage.setNumberOfElements(userPage.getNumberOfElements());
        userProfilePage.setTotalElements(userPage.getTotalElements());
        userProfilePage.setTotalPages(userPage.getTotalPages());
        return userProfilePage;


    }

    public long getTotalMaleUsersCount() {
        return userRepository.getMaleUsersCount();
    }

    public long getTotalFemaleUsersCount() {
        return userRepository.getFemaleUsersCount();
    }


    public PlanWithLimit getUserPlanDetails(User user) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        return featureLimitService.findPlanWithLimitByName(user.getType());
    }
}

