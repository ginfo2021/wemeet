package com.wemeet.dating.service;

import com.wemeet.dating.dao.FeatureLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureLimitService {
    private final FeatureLimitRepository featureLimitRepository;

    @Autowired
    public FeatureLimitService(FeatureLimitRepository featureLimitRepository) {
        this.featureLimitRepository = featureLimitRepository;
    }
}
