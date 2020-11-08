package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteMusicRequest {
    @NotNull
    private Long songId;
}
