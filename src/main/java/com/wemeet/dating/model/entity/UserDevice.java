package com.wemeet.dating.model.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
