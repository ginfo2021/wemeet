package com.wemeet.dating.api;

import com.wemeet.dating.model.request.PaymentRequest;
import com.wemeet.dating.model.request.PaymentWebhookRequest;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.PaymentService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.NotSuspendedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/payment")
@Validated
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping(value = "/webhook",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> callback(@RequestHeader(value = "x-paystack-signature") String paystackSignature, @RequestBody PaymentWebhookRequest webhookRequest){
        //validate request sender - use paystack signature
        paymentService.handleCallback(paystackSignature, webhookRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/webhook",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String webhookRedirect(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String trxref
    ){
        logger.info("reference && trxref", reference, trxref);
        return "payment-complete";
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/upgrade",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse upgradePlan(
            @AuthenticationPrincipal UserResult userResult,
            @RequestBody PaymentRequest request) throws Exception{
        return ApiResponse.builder()
                .message("Upgrade Successful")
                .data(paymentService.upgradeUserPlan(userResult.getUser(), request))
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
                .data(paymentService.verifyUserTransaction(userResult.getUser(), reference))
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
