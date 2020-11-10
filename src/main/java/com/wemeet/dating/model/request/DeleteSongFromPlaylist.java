package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeleteSongFromPlaylist {
    @NotNull
    private Long playlistId;

    @NotNull
    private List<Long> songs;

}
