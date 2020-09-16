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
import org.springframework.web.bind.annotation.*;

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


    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/matches", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getUserMatches(@AuthenticationPrincipal UserResult userResult,
                                      @RequestParam(defaultValue = "0") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched User Matches successfully")
                .data(swipeService.getUserMatches(userResult.getUser(), pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/suggest", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getSwipeSuggestion(@AuthenticationPrincipal UserResult userResult,
                                          @RequestParam(defaultValue = "false") boolean locationFilter) throws Exception {

        return ApiResponse.builder()
                .message("Fetched swipe suggestions successfully")
                .data(swipeService.getSwipeSuggestion(userResult.getUser(), locationFilter))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


}
