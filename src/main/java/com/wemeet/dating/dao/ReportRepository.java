package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Report;
import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReportRepository extends BaseRepository<Report, Long> {

    Page<Report> findByUserOrderByIdDesc(User user, Pageable pageable);

}
