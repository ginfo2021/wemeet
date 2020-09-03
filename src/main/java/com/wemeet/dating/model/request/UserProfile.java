package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.enums.WorkStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

public class UserProfile {

    private Long id;
    private String bio;
    private Gender gender;
    private Date dateOfBirth;
    private WorkStatus workStatus;
    private List<Gender> genderPreference;

    private Integer swipeRadius;
    private Integer minAge;
    private Integer maxAge;


    private String profileImage;
    private List<String> additionalImages;

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<String> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(List<String> additionalImages) {
        this.additionalImages = additionalImages;
    }
}
