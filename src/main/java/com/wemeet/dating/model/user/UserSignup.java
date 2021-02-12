package com.wemeet.dating.model.user;


import com.wemeet.dating.model.enums.Gender;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserSignup {

    @NotBlank
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank
    private String firstName;

    private String lastName;

    @NotNull
    @Size(min = 11, max = 11, message = "Minumum and maximum of 11 characters")
    private String userName;

    @NotBlank
    private String password;


    @NotBlank
    private Gender gender;

    @NotNull
    private Date dateOfBirth;
    @NotBlank
    private String phone;
    private String deviceId;
    private Double longitude;
    private Double latitude;
}
