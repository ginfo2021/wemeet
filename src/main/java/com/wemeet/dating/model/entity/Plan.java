package com.wemeet.dating.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private String code;

    private Long amount;

    private String period;

    private String currency;

}
