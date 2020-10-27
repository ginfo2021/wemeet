package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PaymentRequest {
    private String email;

    @NotNull
    private String amount;

    @NotNull
    private String plan_code;
}
