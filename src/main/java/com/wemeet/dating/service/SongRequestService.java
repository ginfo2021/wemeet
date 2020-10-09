package com.wemeet.dating.service;

import com.wemeet.dating.dao.SongRequestRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Report;
import com.wemeet.dating.model.entity.SongRequest;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.ReportRequest;
import com.wemeet.dating.model.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SongRequestService {

    private final SongRequestRepository songRequestRepository;
    private final UserService userService;

    @Autowired
    public SongRequestService(SongRequestRepository songRequestRepository, UserService userService) {
        this.songRequestRepository = songRequestRepository;
        this.userService = userService;
    }


    public void requestSong(SongRequest songRequest, User requester) throws Exception {
        if (requester == null || requester.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        songRequest.setRequester(requester);
        songRequest = songRequestRepository.save(songRequest);

    }

    public SongRequest findRequest(Long id) {
        return songRequestRepository.findById(id).orElse(null);
    }

    public PageResponse<SongRequest> getUsersRequests(User user, int pageNum, int pageSize) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new BadRequestException("Reported User does Not exist");
        }
        return new PageResponse<>(songRequestRepository.findByRequesterOrderByIdDesc(user, PageRequest.of(pageNum, pageSize)));

    }

    public PageResponse<SongRequest> getAllRequests(int pageNum, int pageSize) throws Exception {
        return new PageResponse<>(songRequestRepository.findAll(PageRequest.of(pageNum, pageSize)));

    }

    public PageResponse<SongRequest> getRequests(Long userId, int pageNum, int pageSize) throws Exception {
        if (userId == null) {
            return getAllRequests(pageNum, pageSize);
        } else {
            User user = userService.findById(userId);
            return getUsersRequests(user, pageNum, pageSize);
        }
    }










}
