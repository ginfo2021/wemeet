package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.Playlist;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistRepository extends BaseRepository<Playlist, Long> {

    Playlist findBySongId(Music music);

    Playlist findByTitle(String title);

    @Query(value = "select count(*) from playlist where title = :name", nativeQuery = true)
    long countByName(String name);
}
