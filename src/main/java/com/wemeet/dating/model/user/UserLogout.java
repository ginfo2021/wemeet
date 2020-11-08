package com.wemeet.dating.model.user;

import lombok.Data;

@Data
public class UserLogout {
    private String deviceId;
    private String userEmail;
    private Double longitude;
    private Double latitude;
}
