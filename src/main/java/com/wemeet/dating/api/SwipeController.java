package com.wemeet.dating.api;

import com.wemeet.dating.model.request.SwipeRequest;
import com.wemeet.dating.model.request.UserProfile;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.SwipeService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.NotSuspendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/swipe")
@Validated
public class SwipeController {

    private final SwipeService swipeService;

    @Autowired
    public SwipeController(SwipeService swipeService) {
        this.swipeService = swipeService;
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse swipe(@Valid @RequestBody SwipeRequest swipeRequest,
                                         @AuthenticationPrincipal UserResult userResult) throws Exception {
        return ApiResponse.builder()
                .message("Swipe successful")
                .data(swipeService.swipe(swipeRequest, userResult.getUser()))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

}
