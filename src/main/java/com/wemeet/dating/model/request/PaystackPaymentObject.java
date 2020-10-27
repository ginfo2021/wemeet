package com.wemeet.dating.model.request;

import lombok.Data;

@Data
public class PaystackPaymentObject {
    private String email;
    private String amount;
    private String plan;
}
