package com.wemeet.dating.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackPlan {
    private String name;
    private String plan_code;
    private Long amount;
    private String interval;
    private String currency;
    private Long id;

    public PaystackPlan(){
    }
}
