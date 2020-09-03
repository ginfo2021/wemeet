package com.wemeet.dating.api;


import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.model.request.UserProfile;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.UserService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.NotSuspendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/user")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/profile",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateUserProfile(@Valid @RequestBody UserProfile profileRequest,
                             @AuthenticationPrincipal UserResult userResult) throws BadRequestException {
        profileRequest.setId(userResult.getUser().getId());
        return new ApiResponse.ResponseBuilder()
                .setMessage("Successfully saved user profile")
                .setData(userService.updateUserProfile(profileRequest))
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUserProfile(@AuthenticationPrincipal UserResult userResult) throws BadRequestException {

        return new ApiResponse.ResponseBuilder()
                .setMessage("Fetched UserDetails successfully")
                .setData(userService.getProfile(userResult.getUser().getId()))
                .setResponseCode(ResponseCode.SUCCESS)
                .build();
    }

}
