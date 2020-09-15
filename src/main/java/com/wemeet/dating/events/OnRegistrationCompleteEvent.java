package com.wemeet.dating.events;

import com.wemeet.dating.model.entity.EmailVerification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper=true)
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private EmailVerification emailVerification;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param emailVerification the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public OnRegistrationCompleteEvent(EmailVerification emailVerification) {
        super(emailVerification);
        this.emailVerification = emailVerification;
    }
}
