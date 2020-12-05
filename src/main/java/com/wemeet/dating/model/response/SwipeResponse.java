package com.wemeet.dating.model.response;

import com.wemeet.dating.model.entity.Swipe;
import lombok.Data;

@Data
public class SwipeResponse {

    private boolean match;
    private Swipe swipe;
}
