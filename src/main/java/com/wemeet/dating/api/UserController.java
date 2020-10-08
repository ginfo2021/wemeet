package com.wemeet.dating.api;


import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.request.UserImageRequest;
import com.wemeet.dating.model.request.UserLocationRequest;
import com.wemeet.dating.model.request.UserProfile;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.UserService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.AdminUser;
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
                                         @AuthenticationPrincipal UserResult userResult) throws Exception {
        profileRequest.setId(userResult.getUser().getId());
        return ApiResponse.builder()
                .message("Successfully saved user profile")
                .data(userService.updateUserProfile(profileRequest, userResult.getUser()))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUserProfile(@AuthenticationPrincipal UserResult userResult) throws Exception {

        return ApiResponse.builder()
                .message("Fetched UserDetails successfully")
                .data(userService.getProfile(userResult.getUser()))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/location",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateUserLocation(@Valid @RequestBody UserLocationRequest locationRequest,
                                          @AuthenticationPrincipal UserResult userResult) throws Exception {

        userService.updateUserLocation(locationRequest, userResult.getUser());
        return ApiResponse.builder()
                .message("Successfully updated user location")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/profile/image",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse updateUserImages(@Valid @RequestBody UserImageRequest imageRequest,
                                        @AuthenticationPrincipal UserResult userResult) throws Exception {

        userService.updateUserImages(imageRequest, userResult.getUser());
        return ApiResponse.builder()
                .message("Successfully updated user images")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/admin-delete",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteUserAdmin(@AuthenticationPrincipal UserResult userResult, @RequestParam(value = "userId") Long userId) throws Exception {
        User user = userService.findById(userId);
        userService.deleteUser(user, DeleteType.ADMIN);
        return ApiResponse
                .builder()
                .message("Successfully deleted account")
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }

}
