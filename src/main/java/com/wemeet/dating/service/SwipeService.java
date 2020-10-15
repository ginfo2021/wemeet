package com.wemeet.dating.service;

import com.wemeet.dating.dao.SwipeRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.exception.PreferenceNotSetException;
import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.enums.SwipeType;
import com.wemeet.dating.model.request.SwipeRequest;
import com.wemeet.dating.model.request.UserProfile;
import com.wemeet.dating.model.response.PageResponse;
import com.wemeet.dating.model.response.SwipeResponse;
import com.wemeet.dating.util.LocationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwipeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SwipeRepository swipeRepository;
    private final UserService userService;
    private final UserPreferenceService userPreferenceService;
    private final PushNotificationService pushNotificationService;


    @Value("${swipe.suggestion.number}")
    private int wemeetSwipeSuggestionNumber;

    @Autowired
    public SwipeService(SwipeRepository swipeRepository, UserService userService, UserPreferenceService userPreferenceService, PushNotificationService pushNotificationService) {
        this.swipeRepository = swipeRepository;
        this.userService = userService;
        this.userPreferenceService = userPreferenceService;
        this.pushNotificationService = pushNotificationService;
    }


    public SwipeResponse swipe(SwipeRequest swipeRequest, User user) throws Exception {
        SwipeResponse response = new SwipeResponse();
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User swipee = userService.findById(swipeRequest.getSwipeeId());
        if (swipee == null || swipee.getId() <= 0) {
            throw new BadRequestException("Swiped User does Not exist");
        }

        if (swipee.getId().equals(user.getId())) {
            throw new BadRequestException(("User cannot swipe itself"));
        }
        Swipe swipe = new Swipe();
        swipe.setType(swipeRequest.getType());
        swipe.setSwipee(swipee);
        swipe.setSwiper(user);
        try {
            swipe = swipeRepository.save(swipe);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("User has already been swiped by current user");
        }
        if (swipe.getType().equals(SwipeType.LIKE)) {
            Swipe counterSwipe = swipeRepository.findBySwiperAndSwipee(swipee, user);
            if (counterSwipe != null && counterSwipe.getType().equals(SwipeType.LIKE)) {
                response.setMatch(true);
                //TODO: SEND NOTIFICATION TO MATCHED USER(SWIPEE)
                pushNotificationService.pushNotification("You have a new match!", "user");
            }
        }
        response.setSwipe(swipe);

        return response;
    }

    public Swipe findSwipe(Long id) {
        return swipeRepository.findById(id).orElse(null);
    }

    public PageResponse<UserProfile> getUserMatches(User user, int pageNum, int pageSize) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        List<UserProfile> userProfiles = new ArrayList<>();
        PageResponse<UserProfile> userProfilePage = new PageResponse<>();
        UserPreference userPreference = userPreferenceService.findUserPreference(user.getId());
        Page<BigInteger> matchlist = swipeRepository.findSwipersByType(user.getId(), SwipeType.LIKE.getName(), PageRequest.of(pageNum, pageSize));


        matchlist.toList().forEach(a -> {
            try {
                userProfiles.add(getProfileWithUserDistance(a.longValue(), userPreference));
            } catch (Exception e) {
                logger.error("Error fetching user profile for user id: " + a, e);
            }
        });

        userProfilePage.setContent(userProfiles);
        userProfilePage.setPageNum(matchlist.getNumber());
        userProfilePage.setPageSize(matchlist.getSize());
        userProfilePage.setNumberOfElements(matchlist.getNumberOfElements());
        userProfilePage.setTotalElements(matchlist.getTotalElements());
        userProfilePage.setTotalPages(matchlist.getTotalPages());

        return userProfilePage;
    }


    public List<UserProfile> getSwipeSuggestion(User user, boolean filterByDistance) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        List<UserProfile> userProfiles = new ArrayList<>();
        UserPreference userPreference = userPreferenceService.findUserPreference(user.getId());

        if (userPreference.getGenderPreference() == null
                || userPreference.getGenderPreference().isEmpty()
                || userPreference.getMinAge() == null
                || userPreference.getMaxAge() == null) {
            throw new PreferenceNotSetException("User has not set preferences, Update profile");
        }

        List<BigInteger> swipeSuggestions =
                swipeRepository.findSwipeSuggestions(user.getId(), userPreference.getGenderPreference().stream().map(Gender::getName).collect(Collectors.toList()), wemeetSwipeSuggestionNumber);

        List<UserProfile> finalUserProfiles = userProfiles;
        swipeSuggestions.forEach(a -> {
            try {
                finalUserProfiles.add(getProfileWithUserDistance(a.longValue(), userPreference));
            } catch (Exception e) {
                logger.error("Error fetching user profile for user id: " + a, e);
            }
        });

        //Filter by age
        userProfiles = finalUserProfiles.stream()
                .filter(suggestion -> userPreference.getMinAge() <= suggestion.getAge())
                .filter(suggestion -> userPreference.getMaxAge() >= suggestion.getAge())
                .collect(Collectors.toList());
        //Filter by distance/location
        if (filterByDistance) {
            userProfiles = userProfiles.stream()
                    .filter(suggestion -> (suggestion.getDistanceInKm() == null || suggestion.getDistanceInKm() <= 0 || userPreference.getSwipeRadius() >= suggestion.getDistanceInKm()))
                    .collect(Collectors.toList());
        }


        return userProfiles;
    }

    public UserProfile getProfileWithUserDistance(Long userId, UserPreference requestingUserPreference) throws Exception {
        if (requestingUserPreference == null || requestingUserPreference.getId() <= 0) {
            throw new Exception("Other User preference is Invalid");
        }
        UserProfile userProfile = userService.getProfile(userId);
        if (requestingUserPreference.getLatitude() != null && requestingUserPreference.getLatitude() != null && userProfile.getLatitude() != null && userProfile.getLatitude() != null) {
            userProfile.setDistanceInKm(
                    LocationUtils.calculateDistanceInKilometer(
                            requestingUserPreference.getLatitude(),
                            requestingUserPreference.getLongitude(),
                            userProfile.getLatitude(),
                            userProfile.getLongitude())
            );
            userProfile.setDistanceInMiles(
                    LocationUtils.calculateDistanceInMiles(
                            requestingUserPreference.getLatitude(),
                            requestingUserPreference.getLongitude(),
                            userProfile.getLatitude(),
                            userProfile.getLongitude())

            );
        }

        return userProfile;
    }


    public boolean usersMatch(User firstUser, User secondUser) {
        boolean match = true;
        Swipe oneLikes2 = swipeRepository.findBySwiperAndSwipee(firstUser, secondUser);
        if (oneLikes2 == null || oneLikes2.getType() == null || !oneLikes2.getType().equals(SwipeType.LIKE)) {
            match = false;
        }

        Swipe twoLikes1 = swipeRepository.findBySwiperAndSwipee(secondUser, firstUser);
        if (twoLikes1 == null || twoLikes1.getType() == null || !twoLikes1.getType().equals(SwipeType.LIKE)) {
            match = false;
        }


        return match;
    }

}