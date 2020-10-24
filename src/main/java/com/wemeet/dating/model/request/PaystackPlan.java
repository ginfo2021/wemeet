package com.wemeet.dating.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackPlan {
    @NotNull
    private String name;
    @NotNull
    private String plan_code;
    @NotNull
    private BigInteger amount;
    @NotNull
    private String interval;
    private String currency;
    private BigInteger id;

    public PaystackPlan(){
    }
}
