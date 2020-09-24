package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wemeet.dating.model.enums.MessageType;
import com.wemeet.dating.model.enums.SwipeType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime sentAt;


}
