package com.wemeet.dating.api;

import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.DeleteType;
import com.wemeet.dating.model.request.*;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.*;
import com.wemeet.dating.util.ResyncUtil;
import com.wemeet.dating.util.validation.constraint.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/admin")
@Validated
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;
    private final SongRequestService songRequestService;
    private final StorageService storageService;
    private final DashboardService dashboardService;
    private final MusicService musicService;
    private final ResyncUtil resyncUtil;
    private final PaymentService paymentService;

    @Autowired
    public AdminController(
            UserService userService,
            ReportService reportService,
            SongRequestService songRequestService,
            DashboardService dashboardService,
            StorageService storageService,
            MusicService musicService,
            PaymentService paymentService,
            ResyncUtil resyncUtil) {
        this.userService = userService;
        this.reportService = reportService;
        this.songRequestService = songRequestService;
        this.dashboardService = dashboardService;
        this.storageService = storageService;
        this.musicService = musicService;
        this.paymentService = paymentService;
        this.resyncUtil = resyncUtil;
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/resync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse resync(@AuthenticationPrincipal UserResult userResult) throws Exception {

        resyncUtil.resyncDataBaseProperties();
        resyncUtil.resyncConfigProperties();
        return ApiResponse.builder()
                .message("Re-synchronised successfully")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getUserProfile(@AuthenticationPrincipal UserResult userResult, @RequestParam(value = "userId") Long userId) throws Exception {

        return ApiResponse.builder()
                .message("Fetched UserDetails successfully")
                .data(userService.getProfile(userId))
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
    @GetMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getReports(@AuthenticationPrincipal UserResult userResult,
                                        @RequestParam(value = "userId", required = false) Long userId,
                                        @RequestParam(defaultValue = "0") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(reportService.getReports(userId, pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/suspended-users", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getsuspendedUsers(@AuthenticationPrincipal UserResult userResult,
                                         @RequestParam(defaultValue = "0") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(userService.getsuspendedUsers(pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/song-requests", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getSongRequests(@AuthenticationPrincipal UserResult userResult,
                                        @RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(defaultValue = "0") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(songRequestService.getRequests(userId, description, pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @DeleteMapping(value = "/song-request", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse deleteSongRequests(@AuthenticationPrincipal UserResult userResult,
                                       @RequestParam(value = "id") List<Long> requestIds) throws Exception {

        songRequestService.deleteSongRequests(requestIds);
        return ApiResponse.builder()
                .message("Deleted successfully")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse dashboard(@AuthenticationPrincipal UserResult userResult) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(dashboardService.getDashBoardCount())
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getAllUsers(@AuthenticationPrincipal UserResult userResult,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(defaultValue = "0") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(userService.getAllUsers(name, pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/music/upload", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse uploadMusic(@AuthenticationPrincipal UserResult userResult,
                                   @Validated @ModelAttribute MusicUploadRequest request
                                   ) throws Exception {

        storageService.storeMusicFiles(userResult, request);

        return ApiResponse.builder()
                .message("Uploaded  successfully")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/create/playlist", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse createPlaylist(
            @AuthenticationPrincipal UserResult userResult,
            @RequestBody CreatePlaylistRequest request
    ) throws Exception {

        musicService.createOrUpdatePlaylist(userResult.getUser(), request);

        return ApiResponse.builder()
                .message("Uploaded  successfully")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/delete/song", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteSongFromPlaylist(
            @AuthenticationPrincipal UserResult userResult,
            @RequestBody DeleteSongFromPlaylist request
    ) throws Exception {

        musicService.deleteSongFromPlaylist(userResult.getUser(), request);

        return ApiResponse.builder()
                .message("Deleted  successfully")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/delete/music", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse deleteMusic(@AuthenticationPrincipal UserResult userResult,
                                   @RequestBody DeleteMusicRequest request
    ) throws Exception {

        musicService.deleteMusic(userResult.getUser(), request);

        return ApiResponse.builder()
                .message("Deleted successfully")
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/music/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getMusicList(@AuthenticationPrincipal UserResult userResult,
                                    @RequestParam(required = false) String title,
                                    @RequestParam(defaultValue = "0") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(musicService.getMusicList(title, userResult.getUser(), pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/playlist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getPlaylist(@AuthenticationPrincipal UserResult userResult,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(defaultValue = "0") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(musicService.getPlaylist(title, userResult.getUser(), pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

    @GetMapping(value = "/songs/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse countSongs(@AuthenticationPrincipal UserResult userResult
                                  ) throws Exception {

        return ApiResponse.builder()
                .message("Fetched  successfully")
                .data(musicService.countSongs())
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @AdminUser(message = "Current User Not Admin")
    @GetMapping(value = "/plans", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getPlans(@AuthenticationPrincipal UserResult userResult) throws Exception {

        return ApiResponse.builder()
                .message("Plans retrieved successfully")
                .data(paymentService.getPlans(userResult.getUser()))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @AdminUser(message = "Current User Not Admin")
    @PostMapping(value = "/plan/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse createPlan(@AuthenticationPrincipal UserResult userResult,
                                      @RequestBody CreatePlanRequest request) throws Exception {

        return ApiResponse.builder()
                .message("Plan created successfully")
                .data(paymentService.createPlan(userResult.getUser(), request))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }

}
