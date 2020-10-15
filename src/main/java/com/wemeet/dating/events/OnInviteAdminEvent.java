package com.wemeet.dating.events;

import com.wemeet.dating.model.entity.AdminInvite;
import com.wemeet.dating.model.entity.EmailVerification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class OnInviteAdminEvent extends ApplicationEvent {

    private AdminInvite adminInvite;


    public OnInviteAdminEvent(AdminInvite adminInvite) {
        super(adminInvite);
        this.adminInvite = adminInvite;
    }
}
