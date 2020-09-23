package com.wemeet.dating.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class FileUploadRequest {
    @NotNull
    private String imageType;
    @NotNull
    private MultipartFile file;
}
