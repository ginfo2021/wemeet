package com.wemeet.dating.service;

import com.wemeet.dating.dao.MessageRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.exception.UsersNotMatchedException;
import com.wemeet.dating.model.entity.Message;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.MessageRequest;
import com.wemeet.dating.model.response.MessageResponse;
import com.wemeet.dating.model.response.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SwipeService swipeService;
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final PushNotificationService pushNotificationService;

    @Autowired
    public MessageService(SwipeService swipeService, UserService userService, MessageRepository messageRepository, PushNotificationService pushNotificationService) {
        this.swipeService = swipeService;
        this.userService = userService;
        this.messageRepository = messageRepository;
        this.pushNotificationService = pushNotificationService;
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

        if (!swipeService.usersMatch(user, receiver)) {
            throw new UsersNotMatchedException("You can only send messages to matched user");
        }
        Message message = new Message();
        message.setType(messageRequest.getType());
        message.setSender(user);
        message.setReceiver(receiver);
        message.setContent(messageRequest.getContent());

        message = messageRepository.save(message);

        //TODO: SEND NOTIFICATION TO RECEIVER
        pushNotificationService.pushNotification("You have a new like!", "test");
        return new MessageResponse(message);

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
