package com.wemeet.dating.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.wemeet.dating.events.OnGeneratePasswordToken;
import com.wemeet.dating.events.OnInviteAdminEvent;
import com.wemeet.dating.events.OnRegistrationCompleteEvent;
import com.wemeet.dating.model.entity.AdminInvite;
import com.wemeet.dating.model.entity.EmailVerification;
import com.wemeet.dating.model.entity.ForgotPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private MailSender mailSender;

    @Value("${spring.sendgrid.api-key}")
    private String sendgridApiKey;

    @Value("${mail.default.sender}")
    private String sender;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @EventListener
    public void confirmRegistration(OnRegistrationCompleteEvent event) {
       try {
           EmailVerification emailVerification = event.getEmailVerification();
           String subject = "Registration Confirmation";

           String message = "copy token" + " " + emailVerification.getToken();

           Personalization personalization = new Personalization();
           personalization.addDynamicTemplateData("name", emailVerification.getUserEmail());
           personalization.addDynamicTemplateData("token", message);
           personalization.addTo(new Email(emailVerification.getUserEmail()));

           this.sendgridMailer("d-cbbaa9a4cc4648cc8643d4ef85508d45", personalization);
       }catch(Exception exception){
           logger.error("Unable to send registration email", exception);
       }

    }


    @EventListener
    public void inviteAdmin(OnInviteAdminEvent event) {
        try {
            AdminInvite adminInvite = event.getAdminInvite();
            String subject = "Admin Invitation";

            String message = "You've been invited copy token" + " " + adminInvite.getToken();
            //move token creation here
            Personalization personalization = new Personalization();
            personalization.addDynamicTemplateData("name", adminInvite.getUserEmail());
            personalization.addDynamicTemplateData("token", message);
            personalization.addTo(new Email(adminInvite.getUserEmail()));

            this.sendgridMailer("d-cbbaa9a4cc4648cc8643d4ef85508d45", personalization);
        }catch(Exception exception){
            logger.error("Unable to send admin invitation email", exception);
        }

    }

    @EventListener
    public void resetPassword(OnGeneratePasswordToken event) {
        try {
            ForgotPassword forgotPassword = event.getForgotPassword();
            String subject = "Password Reset Confirmation";

            String message = "copy token" + " " + forgotPassword.getToken();
            Personalization personalization = new Personalization();
            personalization.addDynamicTemplateData("name", forgotPassword.getUser().getFirstName());
            personalization.addDynamicTemplateData("token", message);
            personalization.addTo(new Email(forgotPassword.getUser().getEmail()));

            this.sendgridMailer("d-cbbaa9a4cc4648cc8643d4ef85508d45", personalization);

        }catch(Exception exception){
            logger.error("Unable to send reset password email", exception);
        }

    }


    public void sendgridMailer(String templateId, Personalization personalization) {
        try {
            //move token creation here
            Mail mail = new Mail();
            mail.setFrom(new Email(sender));
            mail.setTemplateId(templateId);
            mail.addPersonalization(personalization);

            SendGrid sg = new SendGrid(sendgridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }catch(Exception exception){
            logger.error("Unable to send reset password email", exception);
        }

    }

}
