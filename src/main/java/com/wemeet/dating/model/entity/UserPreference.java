package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.enums.WorkStatus;
import com.wemeet.dating.util.converters.GenderListConverter;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user_preference")
public class UserPreference {

    @Id
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(columnDefinition="TEXT")
    private String bio;
    private Double longitude;
    private Double latitude;
    @Enumerated(EnumType.STRING)
    private WorkStatus workStatus;

    @Convert(converter = GenderListConverter.class)
    private List<Gender> genderPreference;
    @Column(nullable = false, columnDefinition="boolean default true")
    private boolean showLocation;
    @Column(nullable = false)
    private boolean hideProfile;

    private Integer swipeRadius;
    private Integer minAge;
    private Integer maxAge;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @Column(nullable = false)
    private Date dateCreated;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastUpdated;

}
