package com.wemeet.dating.api;

import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.ReportService;
import com.wemeet.dating.service.UserService;
import com.wemeet.dating.util.validation.constraint.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/admin")
@Validated
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;

    @Autowired
    public AdminController(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUserProfile(@AuthenticationPrincipal UserResult userResult, @RequestParam(value = "userId") Long userId) throws Exception {

        User user = userService.findById(userId);
        return ApiResponse.builder()
                .message("Fetched UserDetails successfully")
                .data(userService.getProfile(user))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/delete-user",
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

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/suspend-account",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse suspendUserAdmin(@AuthenticationPrincipal UserResult userResult, @RequestParam(value = "userId") Long userId) throws Exception {
        User user = userService.findById(userId);
        userService.suspendUser(user);
        return ApiResponse
                .builder()
                .message("Successfully suspended account")
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/restore-account",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse restoreUserAdmin(@AuthenticationPrincipal UserResult userResult, @RequestParam(value = "userId") Long userId) throws Exception {
        User user = userService.findById(userId);
        userService.restoreUser(user);
        return ApiResponse
                .builder()
                .message("Successfully restored account")
                .responseCode(ResponseCode.SUCCESS)
                .build();

    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/reported-users", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getReportedUsers(@AuthenticationPrincipal UserResult userResult,
                                        @RequestParam(value = "userId", required = false) Long userId,
                                        @RequestParam(defaultValue = "0") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched messages successfully")
                .data(reportService.getReportedUsers(userId, pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/suspended-users", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getsuspendedUsers(@AuthenticationPrincipal UserResult userResult,
                                         @RequestParam(defaultValue = "0") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched messages successfully")
                .data(userService.getsuspendedUsers(pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


}
