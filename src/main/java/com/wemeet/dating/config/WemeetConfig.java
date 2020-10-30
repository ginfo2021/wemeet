package com.wemeet.dating.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class WemeetConfig {

    @Value("${security.jwt.token.expire.hour}")
    private long wemeetJwtvalidityInMilliseconds;

    @Value("${swipe.suggestion.number}")
    private int wemeetSwipeSuggestionNumber;

    @Value("${default.swipe.limit}")
    private int wemeetDefaultSwipeLimit;

    @Value("${default.message.limit}")
    private int wemeetDefaultMessageLimit;

    @Value("${default.update.location}")
    private boolean wemeetDefaultUpdateLocation;

    @Value("${paystack.secret.key}")
    private String paystackKey;

    @Value("${paystack.base.url}")
    private String paystackBaseUrl;

    @Value("${default.plan.name}")
    private String wemeetDefaultPlanName;

    @Value("${default.plan.code}")
    private String weMeetDefaultPlanCode;

}
