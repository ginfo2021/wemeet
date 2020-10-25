package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Plan;

public interface PlanRepository extends BaseRepository<Plan, Long> {
    Plan findByCode(String code);
}
