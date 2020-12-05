package com.wemeet.dating.api;


import com.wemeet.dating.model.request.NotificationRequest;
import com.wemeet.dating.model.request.UserImageRequest;
import com.wemeet.dating.model.request.UserLocationRequest;
import com.wemeet.dating.model.request.UserProfile;
import com.wemeet.dating.model.entity.SongRequest;
import com.wemeet.dating.model.request.*;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.*;
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
    private final ReportService reportService;
    private final BlockService blockService;
    private final SongRequestService songRequestService;

    private final PushNotificationService pushNotificationService;

    @Autowired
    public UserController(UserService userService,
                          PushNotificationService pushNotificationService,
                          ReportService reportService,
                          BlockService blockService, SongRequestService songRequestService) {
        this.userService = userService;
        this.pushNotificationService = pushNotificationService;
        this.reportService = reportService;
        this.blockService = blockService;
        this.songRequestService = songRequestService;
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
                .data(userService.getProfileWithPlan(userResult.getUser()))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUserPlanDetails(@AuthenticationPrincipal UserResult userResult) throws Exception {

        return ApiResponse.builder()
                .message("Fetched UserDetails successfully")
                .data(userService.getUserPlanDetails(userResult.getUser()))
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

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/publish",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse publish(@Valid @RequestBody NotificationRequest notificationRequest,
                                        @AuthenticationPrincipal UserResult userResult) throws Exception {
        pushNotificationService.sendPushNotificationToToken(notificationRequest);
        return ApiResponse.builder()
                .message("Successfully published message")
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/report",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse report(@Valid @RequestBody ReportRequest reportRequest,
                              @AuthenticationPrincipal UserResult userResult) throws Exception {
        reportService.report(reportRequest, userResult.getUser());
        return ApiResponse.builder()
                .message("Report successful")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/song-request",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse requestSong(@Valid @RequestBody SongRequest songRequest,
                              @AuthenticationPrincipal UserResult userResult) throws Exception {
        songRequestService.requestSong(songRequest, userResult.getUser());
        return ApiResponse.builder()
                .message("Request successful")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/block",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse block(@RequestParam(value = "userId") Long userId,
                              @AuthenticationPrincipal UserResult userResult) throws Exception {
        blockService.block(userId, userResult.getUser());
        return ApiResponse.builder()
                .message("Block successful")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/unblock",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse unblock(@RequestParam(value = "userId") Long userId,
                               @AuthenticationPrincipal UserResult userResult) throws Exception {
        blockService.unBlock(userId, userResult.getUser());
        return ApiResponse.builder()
                .message("Report successful")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/blocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUserBlocks(@AuthenticationPrincipal UserResult userResult,
                                      @RequestParam(defaultValue = "0") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched User blocks successfully")
                .data(blockService.getUserBlocks(userResult.getUser(), pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

}
