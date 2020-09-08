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
        return ApiResponse
                .builder()
                .message("Successfully Verified Email")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @PostMapping(value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse signUp(@Valid @RequestBody UserSignup userSignup) throws Exception {

        return ApiResponse
                .builder()
                .message("User Signed Up Successfully")
                .data(authService.signUp(userSignup))
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }


    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse login(@Valid @RequestBody UserLogin login) throws InvalidCredentialException {
        return ApiResponse.builder()
                .message("Login Successful")
                .data(authService.login(login))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUser(@AuthenticationPrincipal UserResult userResult) {

        return ApiResponse
                .builder()
                .message("Fetched User successfully")
                .data(userResult)
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @GetMapping(value = "/accounts/forgot-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse forgotPassword(@RequestParam String email) throws Exception {
        authService.generatePasswordToken(email);
        return ApiResponse
                .builder()
                .message("Please check your email to reset password")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @GetMapping(value = "/accounts/verify-password-token",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse passwordTokenVerification(@RequestParam String token, @RequestParam String email) {
        return ApiResponse
                .builder()
                .data(authService.verifyForgotPasswordToken(token, email))
                .message("Verification completed")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @PostMapping(value = "/accounts/reset-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse resetPassword(@RequestBody ResetPasswordRequest resetPassword) throws Exception {
        authService.resetPassword(resetPassword);
        return ApiResponse
                .builder()
                .message("Password Successfully Reset")
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }

    @PostMapping(value = "/change-password",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse changePassword(@RequestBody ChangePasswordRequest changePassword, @AuthenticationPrincipal UserResult userResult) throws Exception {
        authService.changePassword(changePassword, userResult.getUser());
        return ApiResponse
                .builder()
                .message("Password Successfully Changed")
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }


    @PostMapping(value = "/self-delete",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteUser(@AuthenticationPrincipal UserResult userResult) throws Exception {
        authService.deleteUser( userResult.getUser());
        return ApiResponse
                .builder()
                .message("Successfully deleted account")
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }




}
