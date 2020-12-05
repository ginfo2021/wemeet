package com.wemeet.dating.model.request;

import com.wemeet.dating.model.entity.FeatureLimit;
import lombok.Data;

@Data
public class CreatePlanRequest {
    private String name;
    private String interval;
    private String amount;
    private FeatureLimit limits;
}
