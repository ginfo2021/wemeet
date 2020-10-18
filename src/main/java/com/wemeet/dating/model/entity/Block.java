package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wemeet.dating.model.enums.SwipeType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"})
)
@Entity
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", referencedColumnName = "id", nullable = false)
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", referencedColumnName = "id", nullable = false)
    private User blocked;


    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(nullable = false)
    private Date dateCreated;
}
