package com.wemeet.dating.service;

import com.wemeet.dating.events.OnGeneratePasswordToken;
import com.wemeet.dating.events.OnRegistrationCompleteEvent;
import com.wemeet.dating.model.entity.EmailVerification;
import com.wemeet.dating.model.entity.ForgotPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private MailSender mailSender;

    @Value("${mail.default.sender}")
    private String sender;

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
           exception.toString();
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
            exception.toString();
        }

    }
}
