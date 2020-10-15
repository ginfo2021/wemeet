package com.wemeet.dating.model.request;

import lombok.Data;

@Data
public class CreatePlaylistRequest {
    private String name;
    private Long songId;
}
