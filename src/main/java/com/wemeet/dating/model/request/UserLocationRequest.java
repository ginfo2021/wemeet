package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserLocationRequest {
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;

}
