package com.wemeet.dating.model.request;

import com.wemeet.dating.model.enums.MusicType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MusicUploadRequest {
    @NotNull
    private List<MultipartFile> files;

    @NotNull
    private MusicType musicType;

    @NotNull
    private String title;

    @NotNull
    private String artist;
}
