package com.wemeet.dating.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationRequest {
    private String title;
    private String message;
    private String topic;
    private String token;  //SAME AS DEVICEID
}
