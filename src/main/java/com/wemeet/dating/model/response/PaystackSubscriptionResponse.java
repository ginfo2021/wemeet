package com.wemeet.dating.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wemeet.dating.model.request.PaystackCustomer;
import com.wemeet.dating.model.request.PaystackPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackSubscriptionResponse {
    private PaystackPlan  plan;
    private PaystackCustomer customer;
    private String status;
    private String subscription_code;
    private String email_token;
}
