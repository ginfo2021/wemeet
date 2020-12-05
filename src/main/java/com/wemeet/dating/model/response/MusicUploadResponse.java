package com.wemeet.dating.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MusicUploadResponse {
    private String musicUrl;
}
