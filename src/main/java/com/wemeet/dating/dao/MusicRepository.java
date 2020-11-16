package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface MusicRepository extends BaseRepository<Music, Long> {

    Page<Music> findAll(Pageable pageable);

    @Query(value = "select * from music where title like :title order by date_created desc", nativeQuery = true)
    Page<Music> findByTitle(String title, Pageable pageable);
}
