package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class FeatureLimit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_code", referencedColumnName = "code", nullable = false, unique = true)
    private Plan plan;
    @Column(nullable = false)
    int dailySwipeLimit;
    @Column(nullable = false)
    int dailyMessageLimit;
    @Column(nullable = false)
    boolean updateLocation;
}
