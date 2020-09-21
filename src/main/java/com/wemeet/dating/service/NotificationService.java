package com.wemeet.dating.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemeet.dating.events.OnGeneratePasswordToken;
import com.wemeet.dating.events.OnRegistrationCompleteEvent;
import com.wemeet.dating.model.entity.EmailVerification;
import com.wemeet.dating.model.entity.ForgotPassword;
import com.wemeet.dating.model.request.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    @Autowired
    private MailSender mailSender;

    @Autowired
    private AmazonSNS amazonSNS;

    @Value("${sns.topic.arn}")
    private String snsTopicWemeetARN;


    @Value("${mail.default.sender}")
    private String sender;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @EventListener
    public void confirmRegistration(OnRegistrationCompleteEvent event) {
       try {
           EmailVerification emailVerification = event.getEmailVerification();
           String subject = "Registration Confirmation";

           String message = "copy token" + " " + emailVerification.getToken();
           //move token creation here
           SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
           simpleMailMessage.setFrom(sender);
           simpleMailMessage.setTo(emailVerification.getUserEmail());
           simpleMailMessage.setSubject(subject);
           simpleMailMessage.setText(message);
           this.mailSender.send(simpleMailMessage);
       }catch(Exception exception){
           logger.error("Unable to send registration email", exception);
       }

    }

    @EventListener
    public void resetPassword(OnGeneratePasswordToken event) {
        try {
            ForgotPassword forgotPassword = event.getForgotPassword();
            String subject = "Password Reset Confirmation";

            String message = "copy token" + " " + forgotPassword.getToken();
            //move token creation here
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(forgotPassword.getUser().getEmail());
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            this.mailSender.send(simpleMailMessage);
        }catch(Exception exception){
            logger.error("Unable to send reset password email", exception);
        }

    }

    public void publishNotification(NotificationRequest notificationrequest)throws Exception {
        try{
            ObjectMapper mapper = new ObjectMapper();
            String notificationMessage = mapper.writeValueAsString(notificationrequest);
            MessageAttributeValue attributeValue = new MessageAttributeValue();
            attributeValue.setDataType("String");
            attributeValue.setStringValue(notificationrequest.getNotificationType());
            Map<String, MessageAttributeValue> messageAttribute = new HashMap<String, MessageAttributeValue>();
            messageAttribute.put("notification_type", attributeValue); //check this
            String snsTopic = getTopicARN(snsTopicWemeetARN);
            PublishRequest publishRequest = new PublishRequest(snsTopic, notificationMessage);
            publishRequest.withMessageAttributes(messageAttribute);
            PublishResult publishResult = this.amazonSNS.publish(publishRequest);
            logger.info("MessageId - ", publishRequest.toString());

        }catch (Exception ex){
            logger.error("Unable to publish message", ex);
        }
    }

    private String getTopicARN(String topic) throws Exception {
        switch (topic) {
            case "wemeet":
                return snsTopicWemeetARN;
            default:
                throw new RuntimeException("No matching topic supported!");
        }
    }
}
