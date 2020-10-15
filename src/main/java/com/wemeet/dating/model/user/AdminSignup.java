package com.wemeet.dating.model.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AdminSignup {

    @NotBlank
    private String token;

    @NotBlank
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;


    @NotBlank
    private String password;


}
