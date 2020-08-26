package com.wemeet.dating.model.user;

import javax.validation.constraints.NotBlank;

public class UserLogin {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private String deviceId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}