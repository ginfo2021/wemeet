package com.wemeet.dating.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemeet.dating.api.PaymentController;
import com.wemeet.dating.dao.*;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.*;
import com.wemeet.dating.model.enums.TransactionType;
import com.wemeet.dating.model.request.*;
import com.wemeet.dating.model.response.PaymentResponse;
import com.wemeet.dating.model.response.PaymentStatusResponse;
import com.wemeet.dating.model.response.PlanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@EnableAsync
public class PaymentService {
    private final PaystackService paystackService;
    private final PlanRepository planRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final WebhookRepository webhookRepository;
    private final UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentService(PaystackService paystackService, PlanRepository planRepository, TransactionRepository transactionRepository, SubscriptionRepository subscriptionRepository, WebhookRepository webhookRepository, UserRepository userRepository) {
        this.paystackService = paystackService;
        this.planRepository = planRepository;
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.webhookRepository = webhookRepository;
        this.userRepository = userRepository;
    }

    public PaystackPlan createPlan(User user, CreatePlanRequest request) throws Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        PaystackPlan planResponse = paystackService.createPlan(request, PaystackPlan.class);

        Plan plan = new Plan();
        plan.setAmount(planResponse.getAmount());
        plan.setCurrency(planResponse.getCurrency());
        plan.setCode(planResponse.getPlan_code());
        plan.setPeriod(planResponse.getInterval());
        plan.setName(planResponse.getName());
        planRepository.save(plan);

        return planResponse;
    }

    public PlanResponse getPlans(User user) throws Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        PlanResponse planResponse = paystackService.getPlans(PlanResponse.class);

        planResponse.getData().forEach(plan->{
            Plan plan1 = planRepository.findByCode(plan.getPlan_code());
            if (plan1 == null){
                plan1 = new Plan();
                plan1.setAmount(plan.getAmount());
                plan1.setCurrency(plan.getCurrency());
                plan1.setCode(plan.getPlan_code());
                plan1.setPeriod(plan.getInterval());
                plan1.setName(plan.getName());
                planRepository.save(plan1);
            }
        });
        return planResponse;
    }

    public PaymentResponse upgradeUserPlan(User user, PaymentRequest request) throws  Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        Transaction transaction = createTransaction(user);

        PaystackPaymentObject paystackPaymentObject = new PaystackPaymentObject();
        paystackPaymentObject.setEmail(user.getEmail());
        paystackPaymentObject.setAmount(request.getAmount());
        paystackPaymentObject.setPlan(request.getPlan_code());

        PaymentResponse paymentResponse = paystackService.initializeTransaction(paystackPaymentObject, PaymentResponse.class);

        updateTransaction(transaction, paymentResponse);

        return paymentResponse;
    }


    public PaymentStatusResponse verifyTransaction(User user, String reference) throws  Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        PaymentStatusResponse paymentStatusResponse = paystackService.verifyTransaction(reference, PaymentStatusResponse.class);
        return paymentStatusResponse;
    }

    private Transaction createTransaction(User user) throws BadRequestException {
        Transaction transaction = transactionRepository.findByUserAndStatus(user, "ongoing");
        if (transaction != null){
            throw new BadRequestException("Possible Duplicate Transaction");
        }

        transaction = new Transaction();
        transaction.setPayment_processor("paystack");
        transaction.setIsSubscription(true);
        transaction.setStatus("created");
        transaction.setTransaction_type(TransactionType.UPGRADE);
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    private void updateTransaction(Transaction transaction, PaymentResponse paymentResponse){
        transaction.setAccess_code(paymentResponse.getAccess_code());
        transaction.setAuthorization_url(paymentResponse.getAuthorization_url());
        transaction.setReference(paymentResponse.getReference());
        transaction.setStatus("ongoing");
        transactionRepository.save(transaction);
    }

    @Async
    public void handleCallback(String paystackSignature, PaymentWebhookRequest request) {
        try {
            paystackService.validatePaystackRequest(paystackSignature, request);

            Webhook webhook = new Webhook();
            webhook.setRequest(objectMapper.writeValueAsString(request));
            webhook = webhookRepository.save(webhook);

            String event = request.getEvent();
            switch (event){
                case "charge.success":
                    processChargeSuccess(webhook, request);
                    break;
                case "subscription.disable":
                    processSubscription(webhook, request);
                    break;
                case "subscription.create":
                    processSubscription(webhook, request);
                    break;
                case "invoice.failed":
                    processInvoiceFailed(webhook, request);
                    break;
                default:
                    webhook.setCompleted(true);
                    webhookRepository.save(webhook);
            }
        } catch(Exception ex){
            logger.error("Webhook Exception", ex);
        }
    }

    private void processInvoiceFailed(Webhook webhook, PaymentWebhookRequest request) {
        logger.info("webhook -  paystack - InvoiceFailed", request);
    }

    private void processChargeSuccess(Webhook webhook, PaymentWebhookRequest request) {
        PaystackTransactionData transactionData = objectMapper.convertValue(request.getData(), PaystackTransactionData.class);

        Transaction transaction = transactionRepository.findByReference(transactionData.getReference());
        if (transaction != null){
            transaction.setStatus(transactionData.getStatus());
            transaction.setAmount(transactionData.getAmount());
            transaction.setPayment_method(transaction.getPayment_method());

            transactionRepository.save(transaction);

            webhook.setCompleted(true);
            webhookRepository.save(webhook);
        }
    }


    private void processSubscription(Webhook webhook, PaymentWebhookRequest request){

        PaystackSubscription paystackSubscription = objectMapper.convertValue(request.getData(), PaystackSubscription.class);

        User user = userRepository.findByEmailAndDeletedIsFalse(paystackSubscription.getCustomer().getEmail());
        Subscription subscription = subscriptionRepository.findByUser(user);

        if (subscription == null){
            subscription = new Subscription();
            subscription.setAmount(paystackSubscription.getAmount());
            subscription.setPlan_code(paystackSubscription.getPlan().getPlan_code());
            subscription.setPurchased(LocalDateTime.parse(paystackSubscription.getCreated_at()));
            subscription.setUser(user);
            subscription.setQuantity(1);
            subscription.setCron_expression(paystackSubscription.getCron_exxpression());
            subscription.setStarted(LocalDateTime.parse(paystackSubscription.getCreated_at()));
            subscription.setNext_payment_date(LocalDateTime.parse(paystackSubscription.getNext_payment_date()));
            subscriptionRepository.save(subscription);

            webhook.setCompleted(true);
            webhookRepository.save(webhook);

        }

        subscription.setAmount(paystackSubscription.getAmount());
        subscription.setPlan_code(paystackSubscription.getPlan().getPlan_code());
        subscription.setPurchased(LocalDateTime.parse(paystackSubscription.getCreated_at()));
        subscription.setUser(user);
        subscription.setQuantity(1);
        subscription.setCron_expression(paystackSubscription.getCron_exxpression());
        subscription.setStarted(LocalDateTime.parse(paystackSubscription.getCreated_at()));
        subscription.setNext_payment_date(LocalDateTime.parse(paystackSubscription.getNext_payment_date()));
        subscriptionRepository.save(subscription);

        webhook.setCompleted(true);
        webhookRepository.save(webhook);

    }

}
