package com.wemeet.dating.model.request;

import lombok.Data;

@Data
public class CreatePlanRequest {
    private String name;
    private String interval;
    private String amount;
}
