package com.wemeet.dating.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AdminLogin {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
