package com.wemeet.dating.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfilePhotoResponse {
    private String imageUrl;
}
