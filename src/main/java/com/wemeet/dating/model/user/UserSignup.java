package com.wemeet.dating.model.user;


import com.wemeet.dating.model.enums.Gender;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UserSignup {

    @NotBlank
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private String userName;

    @NotBlank
    private String password;

    @NotNull
    private Date dateOfBirth;
    @NotBlank
    private String phone;
    private String deviceId;
    private Double longitude;
    private Double latitude;
}
