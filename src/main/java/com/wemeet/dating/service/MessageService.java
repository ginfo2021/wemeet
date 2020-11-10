package com.wemeet.dating.service;

import com.wemeet.dating.config.WemeetConfig;
import com.wemeet.dating.dao.FeatureLimitRepository;
import com.wemeet.dating.dao.MessageRepository;
import com.wemeet.dating.dao.PlanRepository;
import com.wemeet.dating.dao.UserDeviceRepository;
import com.wemeet.dating.exception.*;
import com.wemeet.dating.model.entity.*;
import com.wemeet.dating.model.request.MessageRequest;
import com.wemeet.dating.model.request.NotificationRequest;
import com.wemeet.dating.model.response.MessageResponse;
import com.wemeet.dating.model.response.PageResponse;
import com.wemeet.dating.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SwipeService swipeService;
    private final BlockService blockService;
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final PushNotificationService pushNotificationService;
    private final WemeetConfig wemeetConfig;
    private final PlanRepository planRepository;
    private final FeatureLimitRepository limitRepository;

    @Autowired
    public MessageService(SwipeService swipeService, BlockService blockService, UserService userService, MessageRepository messageRepository, UserDeviceRepository userDeviceRepository, PushNotificationService pushNotificationService, WemeetConfig wemeetConfig, PlanRepository planRepository, FeatureLimitRepository limitRepository) {
        this.swipeService = swipeService;
        this.blockService = blockService;
        this.userService = userService;
        this.messageRepository = messageRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.pushNotificationService = pushNotificationService;
        this.wemeetConfig = wemeetConfig;
        this.planRepository = planRepository;
        this.limitRepository = limitRepository;
    }

    public MessageResponse sendMessage(User user, MessageRequest messageRequest) throws Exception {

        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User receiver = userService.findById(messageRequest.getReceiverId());
        if (receiver == null || receiver.getId() <= 0) {
            throw new BadRequestException("message Receiver User does Not exist");
        }

        if (receiver.getId().equals(user.getId())) {
            throw new BadRequestException(("User cannot message itself"));
        }

        if (blockService.findByBlockerAndBlocked(user, receiver) != null
                || blockService.findByBlockerAndBlocked(receiver, user) != null) {
            throw new BlockedUserException("You have blocked or have been blocked by user");
        }

        if (!swipeService.usersMatch(user, receiver)) {
            throw new UsersNotMatchedException("You can only send messages to matched user");
        }

        validateUserTypeFeatureLimit(user);


        Message message = new Message();
        message.setType(messageRequest.getType());
        message.setSender(user);
        message.setReceiver(receiver);
        message.setContent(messageRequest.getContent());

        message = messageRepository.save(message);

        try {
            List<UserDevice> userDevice = userDeviceRepository.findByUser(receiver);
            userDevice.forEach(userDevice1 -> {
                NotificationRequest notificationRequest = new NotificationRequest();
                notificationRequest.setTitle("Wemeet");
                notificationRequest.setMessage("You have a new message");
                notificationRequest.setToken(userDevice1.getDeviceId());
                pushNotificationService.sendPushNotificationToToken(notificationRequest);
            });

        } catch (Exception ex) {
            logger.error("Unable to send message notification", ex);
        }
        return new MessageResponse(message);

    }

    private void validateUserTypeFeatureLimit(User user) throws UserNotPremiumException {
        Date now = new Date();
        long messagesToday = messageRepository.countBySenderAndSentAtBetween(user, DateUtil.getLocalStartofDay(now), DateUtil.getLocalEndofDay(now));
        Plan plan = planRepository.findByName(user.getType());
        FeatureLimit featureLimit = limitRepository.findByPlan(plan);

        if (featureLimit != null) {
            if (messagesToday >= featureLimit.getDailyMessageLimit() && featureLimit.getDailyMessageLimit() != -1) {
                throw new UserNotPremiumException("You have used up your messages for the day");
            }
        } else {
            if (messagesToday >= wemeetConfig.getWemeetDefaultMessageLimit() && wemeetConfig.getWemeetDefaultMessageLimit() != -1) {
                throw new UserNotPremiumException("You have used up your messages for the day");
            }
        }
    }

    public PageResponse getUsersMessages(User user, long receiverId, int pageNum, int pageSize) throws Exception {
        List<MessageResponse> messageResponseList = new ArrayList<>();
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User receiver = userService.findById(receiverId);
        if (receiver == null || receiver.getId() <= 0) {
            throw new BadRequestException("message Receiver User does Not exist");
        }

        if (!swipeService.usersMatch(user, receiver)) {
            throw new UsersNotMatchedException("You can only view messages of matched user");
        }

        PageResponse<Message> messagePageResponse =
                new PageResponse<>(messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByIdDesc(user.getId(), receiver.getId(), PageRequest.of(pageNum, pageSize)));

        messagePageResponse.getContent().forEach(message -> {
            messageResponseList.add(new MessageResponse(message));
        });


        return PageResponse.builder()
                .content(Collections.singletonList(messageResponseList))
                .totalPages(messagePageResponse.getTotalPages())
                .totalElements(messagePageResponse.getTotalElements())
                .numberOfElements(messagePageResponse.getNumberOfElements())
                .pageNum(messagePageResponse.getPageNum())
                .pageSize(messagePageResponse.getPageSize())
                .build();
    }


}
