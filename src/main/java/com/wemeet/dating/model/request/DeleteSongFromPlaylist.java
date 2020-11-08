package com.wemeet.dating.model.request;

import lombok.Data;

@Data
public class DeleteSongFromPlaylist {
    private Long playlistId;
    private Long songId;
}
