package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.FileType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class MusicUploadRequest {
    @NotNull
    private FileType fileType;

    private MusicCreateRequest musicCreateRequest;

    @NotNull
    private MultipartFile file;
}
