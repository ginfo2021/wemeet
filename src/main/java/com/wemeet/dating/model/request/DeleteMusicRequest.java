package com.wemeet.dating.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeleteMusicRequest {
    @NotNull
    private List<Long> songs;
}
