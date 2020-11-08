package com.wemeet.dating.model.request;

import lombok.Data;

import java.util.List;

@Data
public class CreatePlaylistRequest {
    private String name;
    private List<Long> songs;
}
