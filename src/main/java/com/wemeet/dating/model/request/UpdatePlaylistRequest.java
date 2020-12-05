package com.wemeet.dating.model.request;

import lombok.Data;

@Data
public class UpdatePlaylistRequest {
    private Long id;
    private Long songId;
}
