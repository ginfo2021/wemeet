package com.wemeet.dating.service;

import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.dao.FeatureLimitRepository;
import com.wemeet.dating.dao.PlanRepository;
import com.wemeet.dating.model.entity.FeatureLimit;
import com.wemeet.dating.model.entity.Plan;
import com.wemeet.dating.model.response.PlanWithLimit;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureLimitService {
    private final FeatureLimitRepository featureLimitRepository;
    private final PlanRepository planRepository;
    private final WemeetConfig wemeetConfig;

    @Autowired
    public FeatureLimitService(FeatureLimitRepository featureLimitRepository, PlanRepository planRepository, WemeetConfig wemeetConfig) {
        this.featureLimitRepository = featureLimitRepository;
        this.planRepository = planRepository;
        this.wemeetConfig = wemeetConfig;
    }

    public FeatureLimit save(FeatureLimit featureLimit) {
        return featureLimitRepository.save(featureLimit);
    }

    public FeatureLimit findByPlan(Plan plan) {
        return featureLimitRepository.findByPlan(plan);
    }

    public PlanWithLimit findPlanWithLimitByCode(String code){
        Plan plan = planRepository.findByCode(code);
        PlanWithLimit planWithLimit = new PlanWithLimit();
        BeanUtils.copyProperties(plan, planWithLimit);
        FeatureLimit featureLimit = findByPlan(plan);
        if (featureLimit == null) {
            featureLimit = new FeatureLimit();
            featureLimit.setPlan(plan);
            featureLimit.setDailyMessageLimit(wemeetConfig.getWemeetDefaultMessageLimit());
            featureLimit.setDailySwipeLimit(wemeetConfig.getWemeetDefaultSwipeLimit());
            featureLimit.setUpdateLocation(wemeetConfig.isWemeetDefaultUpdateLocation());
        }
        planWithLimit.setLimits(featureLimit);

        return planWithLimit;
    }

    public PlanWithLimit findPlanWithLimitByName(String name){
        Plan plan = planRepository.findByName(name);
        PlanWithLimit planWithLimit = new PlanWithLimit();
        BeanUtils.copyProperties(plan, planWithLimit);
        FeatureLimit featureLimit = findByPlan(plan);
        if (featureLimit == null) {
            featureLimit = new FeatureLimit();
            featureLimit.setPlan(plan);
            featureLimit.setDailyMessageLimit(wemeetConfig.getWemeetDefaultMessageLimit());
            featureLimit.setDailySwipeLimit(wemeetConfig.getWemeetDefaultSwipeLimit());
            featureLimit.setUpdateLocation(wemeetConfig.isWemeetDefaultUpdateLocation());
        }
        planWithLimit.setLimits(featureLimit);

        return planWithLimit;
    }

}
