package com.wemeet.dating.model.request;

import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.MessageType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageRequest {
    @NotBlank
    private String content;
    private long receiverId;
    @NotNull
    private MessageType type;
}
