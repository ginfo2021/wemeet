package com.wemeet.dating.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaystackCustomer {
    private BigInteger id;
    private String first_name;
    private String last_name;
    @NotNull
    private String email;

    @NotNull
    private String customer_code;
    private String phone;
    private String risk_action;

    public PaystackCustomer(){
    }

}
