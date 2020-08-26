package com.wemeet.dating.service;

import com.wemeet.dating.dao.UserPreferenceRepository;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserPreferenceService {


    private final UserPreferenceRepository userPreferenceRepository;

    private final Logger logger = LoggerFactory.getLogger(UserPreferenceService.class);

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public UserPreference createOrUpdatePreference(UserPreference preference) {
        return userPreferenceRepository.save(preference);
    }

    public UserPreference createBasePreferenceForUser(User user) {
        UserPreference userPreference = new UserPreference();
        userPreference.setId(user.getId());
        return createOrUpdatePreference(userPreference);
    }
}
