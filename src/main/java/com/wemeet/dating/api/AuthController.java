package com.wemeet.dating.api;


import com.wemeet.dating.exception.InvalidCredentialException;
import com.wemeet.dating.model.request.ChangePasswordRequest;
import com.wemeet.dating.model.request.ResetPasswordRequest;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserLogin;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.model.user.UserSignup;
import com.wemeet.dating.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@RestController
@RequestMapping("v1/auth")
@Validated
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


    @GetMapping(value = "/accounts/forgot-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse forgotPassword(@RequestParam String email) throws Exception {
        authService.generatePasswordToken(email);
        return new ApiResponse.ResponseBuilder()
                .setMessage("Please check your email to reset password")
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

    @GetMapping(value = "/accounts/verify-password-token",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse passwordTokenVerification(@RequestParam String token, @RequestParam String email) {
        return new ApiResponse.ResponseBuilder()
                .setData(authService.verifyForgotPasswordToken(token, email))
                .setMessage("Verification completed")
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

    @PostMapping(value = "/accounts/reset-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse resetPassword(@RequestBody @Valid ResetPasswordRequest resetPassword) throws Exception {
        authService.resetPassword(resetPassword);
        return new ApiResponse.ResponseBuilder()
                .setMessage("Password Successfully Reset")
                .setResponseCode(ResponseCode.SUCCESS)
                .build();

    }

    @PostMapping(value = "/change-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse changePassword(@RequestBody @Valid ChangePasswordRequest changePassword, @AuthenticationPrincipal UserResult userResult) throws Exception {
        authService.changePassword(changePassword, userResult.getUser());
        return new ApiResponse.ResponseBuilder()
                .setMessage("Password Successfully Changed")
                .setResponseCode(ResponseCode.SUCCESS)
                .build();

    }


    @PostMapping(value = "/self-delete",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteUser(@AuthenticationPrincipal UserResult userResult) throws Exception {
        authService.deleteUser( userResult.getUser());
        return new ApiResponse.ResponseBuilder()
                .setMessage("Successfully deleted account")
                .setResponseCode(ResponseCode.SUCCESS)
                .build();

    }




}
