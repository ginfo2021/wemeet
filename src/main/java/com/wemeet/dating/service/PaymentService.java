package com.wemeet.dating.service;

import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.CreatePlanRequest;
import com.wemeet.dating.model.request.PaymentRequest;
import com.wemeet.dating.model.request.PaystackPlan;
import com.wemeet.dating.model.response.PaymentResponse;
import com.wemeet.dating.model.response.PaymentStatusResponse;
import com.wemeet.dating.model.response.PlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaystackService paystackService;

    Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaystackService paystackService) {
        this.paystackService = paystackService;
    }

    public PaystackPlan createPlan(User user, CreatePlanRequest request) throws Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        PaystackPlan planResponse = paystackService.createPlan(request, PaystackPlan.class);
        return planResponse;
    };

    public PlanResponse getPlans(User user) throws Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        PlanResponse planResponse = paystackService.getPlans(PlanResponse.class);
        return planResponse;
    };

    public PaymentResponse upgradePlan(User user, PaymentRequest request) throws  Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        //find the users current plan

        PaymentResponse paymentResponse = paystackService.initializeTransaction(request, PaymentResponse.class);
        return paymentResponse;
    }


    public PaymentStatusResponse verifyTransaction(User user, String reference) throws  Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        PaymentStatusResponse paymentStatusResponse = paystackService.verifyTransaction(reference, PaymentStatusResponse.class);
        return paymentStatusResponse;
    }

}
