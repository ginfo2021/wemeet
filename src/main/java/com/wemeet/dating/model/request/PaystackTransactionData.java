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
public class PaystackTransactionData {
    private String id;
    private String status;
    private String reference;
    private Long amount;
    private String gateway_response;
    private String paid_at;
    private String created_at;
    private String channel;
    private String currency;
    private String ip_address;
    private PaystackPlan plan;
    private PaystackCustomer customer;
    private Long requested_amount;
    private String transaction_date;
}
