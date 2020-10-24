package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MusicCreateRequest {
    @NotNull
    private String title;

    @NotNull
    private String artist;

    @NotNull
    private String songUrl;
}
