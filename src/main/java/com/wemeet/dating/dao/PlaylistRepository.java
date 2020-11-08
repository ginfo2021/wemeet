package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.Playlist;

public interface PlaylistRepository extends BaseRepository<Playlist, Long> {

    Playlist findBySongId(Music music);
}
