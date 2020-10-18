package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Block;
import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlockRepository extends BaseRepository<Block, Long> {
    Block findByBlockerAndBlocked(User blocker, User blocked);

    Page<Block> findAllByBlocker(User blocker, Pageable pageable);

}
