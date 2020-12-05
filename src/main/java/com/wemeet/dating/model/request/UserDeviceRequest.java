package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDeviceRequest {
    @NotNull
    private String oldDeviceToken;

    @NotNull
    private String newDeviceToken;
}
