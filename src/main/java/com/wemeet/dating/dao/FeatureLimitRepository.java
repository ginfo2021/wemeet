package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.FeatureLimit;
import com.wemeet.dating.model.entity.Plan;

public interface FeatureLimitRepository extends BaseRepository<FeatureLimit, Long> {

    FeatureLimit findByPlan(Plan plan);
}
