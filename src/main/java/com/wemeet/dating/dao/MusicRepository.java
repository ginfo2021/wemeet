package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MusicRepository extends BaseRepository<Music, Long> {

    Page<Music> findAll(Pageable pageable);

    Page<Music> findByTitle(String title, Pageable pageable);
}
