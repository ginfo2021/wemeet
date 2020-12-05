package com.wemeet.dating.model.response;

import com.wemeet.dating.model.entity.Message;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageResponse {

    private long id;
    private String content;
    private long senderId;
    private long receiverId;
    private MessageType type;
    private LocalDateTime sentAt;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.type = message.getType();
        this.sentAt = message.getSentAt();
    }
}
