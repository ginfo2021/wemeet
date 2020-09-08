package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wemeet.dating.model.enums.AccountType;
import com.wemeet.dating.model.enums.Gender;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String userName;

    private String profileImage;


    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;

    //TODO: validate phone number and make unique
    @Column()
    private String phone;

    @Column(nullable = false)
    private boolean phoneVerified;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Date dateOfBirth;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean suspended;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(nullable = false)
    @JsonIgnore
    private boolean deleted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(nullable = false)
    private Date dateCreated;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(nullable = false)
    private Date lastUpdated;

    public String getName(){
        return (this.firstName + "." + this.lastName).trim();
    }

}
