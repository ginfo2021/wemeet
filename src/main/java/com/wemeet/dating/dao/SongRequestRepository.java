package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.SongRequest;
import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SongRequestRepository extends BaseRepository<SongRequest, Long> {
    Page<SongRequest> findByRequesterOrderByIdDesc(User user, Pageable pageable);

    Page<SongRequest> findByRequesterAndDescriptionContainingIgnoreCaseOrderByIdDesc(User user,String description, Pageable pageable);

    Page<SongRequest> findByDescriptionContainingIgnoreCaseOrderByIdDesc(String description, Pageable pageable);
}
