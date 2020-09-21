package com.wemeet.dating.model.request;

import lombok.Data;

@Data
public class NotificationRequest {
    private String notificationText;
    private String notificationType;
    private String recipientId; //SAME AS DEVICEID
}
