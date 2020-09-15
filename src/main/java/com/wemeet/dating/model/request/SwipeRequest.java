package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.SwipeType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SwipeRequest {


    private long swipeeId;
    @NotNull
    private SwipeType type;
}
