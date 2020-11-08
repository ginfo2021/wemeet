package com.wemeet.dating.service;

import com.wemeet.dating.dao.UserPreferenceRepository;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.user.UserLogin;
import com.wemeet.dating.model.user.UserLogout;
import com.wemeet.dating.model.user.UserSignup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserPreferenceService {


    private final UserPreferenceRepository userPreferenceRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public UserPreference createOrUpdatePreference(UserPreference preference) {
        return userPreferenceRepository.save(preference);
    }

    public UserPreference findUserPreference(Long id) {
        return userPreferenceRepository.findById(id).orElse(null);
    }

    public UserPreference createBasePreferenceForUser(User user, UserSignup userSignup) {
        UserPreference userPreference = new UserPreference();
        userPreference.setId(user.getId());
        userPreference.setLatitude(userSignup.getLatitude());
        userPreference.setLongitude(userSignup.getLongitude());
        return createOrUpdatePreference(userPreference);
    }

    public void updateUserLocation(User user, UserLogin userLogin) {
        if (userLogin.getLatitude() != null && userLogin.getLatitude() != null) {
            UserPreference userPreference = findUserPreference(user.getId());
            userPreference.setLatitude(userLogin.getLatitude());
            userPreference.setLongitude(userLogin.getLongitude());
            createOrUpdatePreference(userPreference);
        }

    }

    public void updateUserLocation(User user, UserLogout userLogout) {
        if (userLogout.getLatitude() != null && userLogout.getLatitude() != null) {
            UserPreference userPreference = findUserPreference(user.getId());
            userPreference.setLatitude(userLogout.getLatitude());
            userPreference.setLongitude(userLogout.getLongitude());
            createOrUpdatePreference(userPreference);
        }

    }
}
