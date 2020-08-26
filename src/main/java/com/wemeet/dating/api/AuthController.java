package com.wemeet.dating.api;


import com.wemeet.dating.exception.InvalidCredentialException;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserLogin;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.model.user.UserSignup;
import com.wemeet.dating.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@RestController
@RequestMapping("v1/auth")
public class AuthController {


    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/verify/email",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse emailVerification(@NotBlank @RequestParam(name = "token") String token) throws Exception {
        authService.verifyEmail(token);
        return new ApiResponse.ResponseBuilder()
                .setMessage("Successfully Verified Email")
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

    @PostMapping(value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse signUp(@Valid @RequestBody UserSignup userSignup) throws Exception {

        return new ApiResponse.ResponseBuilder()
                .setMessage("User Signed Up Successfully")
                .setData(authService.signUp(userSignup))
                .setResponseCode(ResponseCode.SUCCESS)
                .build();

    }


    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse login(@Valid @RequestBody UserLogin login) throws InvalidCredentialException {
        return new ApiResponse.ResponseBuilder()
                .setMessage("Login Successful")
                .setData(authService.login(login))
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUser(@AuthenticationPrincipal UserResult userResult) {

        return new ApiResponse.ResponseBuilder()
                .setMessage("Fetched User successfully")
                .setData(userResult)
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

}
