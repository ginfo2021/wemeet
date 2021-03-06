package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.FileType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class FileUploadRequest {
    @NotNull
    private FileType fileType;
    @NotNull
    private MultipartFile file;
}
