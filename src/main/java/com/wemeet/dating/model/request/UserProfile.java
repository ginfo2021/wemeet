package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.Gender;
import com.wemeet.dating.model.enums.WorkStatus;
import com.wemeet.dating.util.converters.GenderListConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private Long id;
    private String firstName;
    private String lastName;
    private String bio;
    private Gender gender;
    private Date dateOfBirth;
    private WorkStatus workStatus;
    @Convert(converter = GenderListConverter.class)
    private List<Gender> genderPreference;
    private String type;
    private Integer age;
    private Boolean hideLocation;
    private Boolean hideProfile;
    private Double longitude;
    private Double latitude;
    private Integer distanceInKm;
    private Integer distanceInMiles;
    private String email;
    private Boolean emailVerified;
    private String phone;
    private Boolean phoneVerified;
    private Boolean active;
    private Boolean suspended;
    private Date lastSeen;
    private Date dateCreated;
    private Integer swipeRadius;
    @Min(value = 18, message = "minAge should not be less than 18")
    @Max(value = 60, message = "minAge should not be greater than 60")
    private Integer minAge;
    @Min(value = 18, message = "maxAge should not be less than 18")
    @Max(value = 60, message = "maxAge should not be greater than 60")
    private Integer maxAge;


    private String profileImage;
    private List<String> additionalImages;

}
