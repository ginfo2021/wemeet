package com.wemeet.dating.api;

import com.wemeet.dating.model.request.MessageRequest;
import com.wemeet.dating.model.request.SwipeRequest;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ResponseCode;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.MessageService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.NotSuspendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/message")
@Validated
public class MessageController {


    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse sendMessage(@Valid @RequestBody MessageRequest messageRequest,
                             @AuthenticationPrincipal UserResult userResult) throws Exception {
        return ApiResponse.builder()
                .message("Message sent successfully")
                .data(messageService.sendMessage(userResult.getUser(), messageRequest))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }


    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/with", produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse getUsersMessages(@AuthenticationPrincipal UserResult userResult,
                                      @RequestParam(value = "receiverId") long receiverId,
                                      @RequestParam(defaultValue = "0") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        return ApiResponse.builder()
                .message("Fetched messages successfully")
                .data(messageService.getUsersMessages(userResult.getUser(), receiverId, pageNum, pageSize))
                .responseCode(ResponseCode.SUCCESS)
                .build();
    }
}
