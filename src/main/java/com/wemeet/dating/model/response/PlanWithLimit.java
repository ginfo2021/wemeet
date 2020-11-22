package com.wemeet.dating.model.response;

import com.wemeet.dating.model.entity.FeatureLimit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlanWithLimit {
    private String name;
    private String code;
    private Long amount;
    private String period;
    private String currency;
    private boolean currentPlan;
    private FeatureLimit limits;

}
