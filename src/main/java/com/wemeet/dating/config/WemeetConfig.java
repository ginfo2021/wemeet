package com.wemeet.dating.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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


}
