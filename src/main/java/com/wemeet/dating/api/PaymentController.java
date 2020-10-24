package com.wemeet.dating.api;

import com.wemeet.dating.model.request.PaymentRequest;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.PaymentService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.NotSuspendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/payment")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/webhook")
    public void callback(){
        //validate request sender - use paystack signature

    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/init",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse upgradePlan(
            @AuthenticationPrincipal UserResult userResult,
            @RequestBody PaymentRequest request) throws Exception{
        return ApiResponse.builder()
                .message("Payment Successful")
                .data(paymentService.upgradePlan(userResult.getUser(), request))
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/verify",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse verifyTransaction(
            @AuthenticationPrincipal UserResult userResult,
            @RequestParam(required = true) String reference
            ) throws Exception{
        return ApiResponse.builder()
                .message("Payment Successful")
                .data(paymentService.verifyTransaction(userResult.getUser(), reference))
                .build();
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @GetMapping(value = "/plans",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse getPlans(
            @AuthenticationPrincipal UserResult userResult
    ) throws Exception{
        return ApiResponse.builder()
                .message("Plans retrieved Successfully")
                .data(paymentService.getPlans(userResult.getUser()))
                .build();
    }


}
