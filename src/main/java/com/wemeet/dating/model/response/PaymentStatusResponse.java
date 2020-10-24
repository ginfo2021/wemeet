package com.wemeet.dating.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wemeet.dating.model.request.PaystackCustomer;
import com.wemeet.dating.model.request.PaystackPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentStatusResponse {

    private BigInteger id;
    private String status;
    private String reference;
    private BigInteger amount;
    private String gateway_response;
    private String paid_at;
    private String created_at;
    private String channel;
    private String currency;
    private String ip_address;
    private PaystackPlan paystackPlan;
    private PaystackCustomer customer;
    private String plan;
    private BigInteger requested_amount;
    private String transaction_date;

    public PaymentStatusResponse() {
    }
}
