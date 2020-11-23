package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wemeet.dating.model.response.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class WeMeetError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logId;
    private String message;
    private String path;
    @Lob
    private String stackTrace;




    @Enumerated(EnumType.STRING)
    private ResponseCode responseCode;
    private Integer httpResponseCode;

    @CreationTimestamp
    @JsonIgnore
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
