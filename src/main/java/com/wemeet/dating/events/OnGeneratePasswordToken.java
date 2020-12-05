package com.wemeet.dating.events;

import com.wemeet.dating.model.entity.ForgotPassword;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper=true)
public class OnGeneratePasswordToken extends ApplicationEvent {
    private ForgotPassword forgotPassword;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param forgotPassword the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public OnGeneratePasswordToken(ForgotPassword forgotPassword) {
        super(forgotPassword);
        this.forgotPassword = forgotPassword;
    }
}
