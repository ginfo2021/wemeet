package com.wemeet.dating.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLogin {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private String deviceId;
    private Double longitude;
    private Double latitude;
}