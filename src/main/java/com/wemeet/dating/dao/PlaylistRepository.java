package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistRepository extends BaseRepository<Playlist, Long> {

    @Query(value = "select * from playlist where title = :name and song_id = :music", nativeQuery = true)
    Playlist findBySongId(Music music);

    @Query(value = "select * from playlist where title like :title order by date_created desc", nativeQuery = true)
    Page<Playlist> findByTitle(String title, Pageable pageable);

    Playlist findAllByTitle(String title);

    @Query(value = "select count(*) from playlist where title = :name", nativeQuery = true)
    long countByName(String name);

    long countByTitle(String title);

    @Query(value = "SELECT * FROM playlist p inner join music m on m.id = p.song_id where m.deleted = false order by p.date_created desc", nativeQuery = true)
    Page<Playlist> getAllSongsOnPlaylist(Pageable pageable);
}
