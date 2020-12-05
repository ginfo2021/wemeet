package com.wemeet.dating.service;

import com.wemeet.dating.dao.ReportRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Report;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.ReportRequest;
import com.wemeet.dating.model.response.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReportRepository reportRepository;
    private final UserService userService;

    @Autowired
    public ReportService(ReportRepository reportRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }


    public void report(ReportRequest reportRequest, User reporter) throws Exception {
        if (reporter == null || reporter.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User user = userService.findById(reportRequest.getUserId());
        if (user == null || user.getId() <= 0) {
            throw new BadRequestException("Reported User does Not exist");
        }

        if (user.getId().equals(reporter.getId())) {
            throw new BadRequestException(("User cannot report itself"));
        }
        Report report = new Report();
        report.setType(reportRequest.getType());
        report.setUser(user);
        report.setReporter(reporter);
        try {
            report = reportRepository.save(report);
        } catch (DataIntegrityViolationException ex) {
        throw new BadRequestException("You have already reported this user");
    }


    }

    public Report findReport(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    public PageResponse<Report> getUsersReports(User user, int pageNum, int pageSize) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new BadRequestException("Reported User does Not exist");
        }
        return new PageResponse<>(reportRepository.findByUserOrderByIdDesc(user, PageRequest.of(pageNum, pageSize)));

    }

    public PageResponse<Report> getAllReports(int pageNum, int pageSize) throws Exception {
        return new PageResponse<>(reportRepository.findAll(PageRequest.of(pageNum, pageSize)));

    }

    public PageResponse<Report> getReports(Long userId, int pageNum, int pageSize) throws Exception {
        if (userId == null) {
            return getAllReports(pageNum, pageSize);
        } else {
            User user = userService.findById(userId);
            return getUsersReports(user, pageNum, pageSize);
        }
    }

}
