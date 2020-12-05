package com.wemeet.dating.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class MusicUploadRequest {

    @NotNull
    private MultipartFile song;

    @NotNull
    private MultipartFile songArt;

    @NotNull
    private String title;

    @NotNull
    private String artist;
}
