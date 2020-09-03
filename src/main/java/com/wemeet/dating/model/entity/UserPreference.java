package com.wemeet.dating.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.enums.WorkStatus;
import com.wemeet.dating.util.converters.GenderListConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public WorkStatus getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkStatus workStatus) {
        this.workStatus = workStatus;
    }

    public List<Gender> getGenderPreference() {
        return genderPreference;
    }

    public void setGenderPreference(List<Gender> genderPreference) {
        this.genderPreference = genderPreference;
    }

    public Integer getSwipeRadius() {
        return swipeRadius;
    }

    public void setSwipeRadius(Integer swipeRadius) {
        this.swipeRadius = swipeRadius;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


}
