package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "deviceId"})
)
@Entity
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column
    @JsonIgnore
    private String endpointArn;

    @Column
    @JsonIgnore
    private String platform;

    @CreationTimestamp
    @JsonIgnore
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
