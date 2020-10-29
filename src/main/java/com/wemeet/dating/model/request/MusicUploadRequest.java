package com.wemeet.dating.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class MusicUploadRequest {
    private MusicCreateRequest musicCreateRequest;

    @NotNull
    private MultipartFile file;
}
