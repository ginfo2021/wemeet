package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.enums.WorkStatus;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(value = 18, message = "minAge should not be less than 18")
    @Max(value = 60, message = "minAge should not be greater than 60")
    private Integer minAge;
    @Min(value = 18, message = "maxAge should not be less than 18")
    @Max(value = 60, message = "maxAge should not be greater than 60")
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
