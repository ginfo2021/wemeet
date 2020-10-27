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
public class PaystackSubscription {
    private String status;
    private String subscription_code;
    private Long amount;
    private String cron_exxpression;
    private String next_payment_date;
    private PaystackPlan plan;
    private PaystackCustomer customer;
    private String created_at;
}
