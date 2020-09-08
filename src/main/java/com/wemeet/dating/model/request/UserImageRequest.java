package com.wemeet.dating.model.request;

import lombok.Data;

import java.util.List;

@Data
public class UserImageRequest {

    private String profileImage;
    private List<String> additionalImages;

}
