package com.wemeet.dating.service;

import com.wemeet.dating.dao.BlockRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Block;
import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;


import com.wemeet.dating.model.entity.UserPreference;
import com.wemeet.dating.model.enums.SwipeType;
import com.wemeet.dating.model.request.UserProfile;
import com.wemeet.dating.model.response.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlockService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BlockRepository blockRepository;
    private final UserService userService;
    private final SwipeService swipeService;

    @Autowired
    public BlockService(BlockRepository blockRepository, UserService userService, SwipeService swipeService) {
        this.blockRepository = blockRepository;
        this.userService = userService;
        this.swipeService = swipeService;
    }

    @Transactional
    public Block block(Long blockedId, User user) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User blocked = userService.findById(blockedId);
        if (blocked == null || blocked.getId() <= 0) {
            throw new BadRequestException("Blocked User does Not exist");
        }

        if (blocked.getId().equals(user.getId())) {
            throw new BadRequestException(("User cannot block itself"));
        }
        Block block = new Block();
        block.setBlocked(blocked);
        block.setBlocker(user);
        try {
            block = blockRepository.save(block);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("User has already been blocked by current user");
        }
        swipeService.unSwipe(user, blocked);

        return block;
    }

    public void unBlock(Long blockedId, User user) throws Exception{
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User blocked = userService.findById(blockedId);
        if (blocked == null || blocked.getId() <= 0) {
            throw new BadRequestException("Blocked User does Not exist");
        }
        Block block = findByBlockerAndBlocked(user, blocked);
        if(block != null){
            blockRepository.delete(block);
        }
    }


    public Block findByBlockerAndBlocked(User blocker, User blocked) {
        return blockRepository.findByBlockerAndBlocked(blocker, blocked);
    }

    public Block findBlock(Long id) {
        return blockRepository.findById(id).orElse(null);
    }

    public PageResponse<UserProfile> getUserBlocks(User user, int pageNum, int pageSize) throws Exception {

        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }
        List<UserProfile> userProfiles = new ArrayList<>();
        PageResponse<UserProfile> userProfilePage = new PageResponse<>();
        //UserPreference userPreference = userPreferenceService.findUserPreference(user.getId());
        Page<Block> matchlist = blockRepository.findAllByBlocker(user, PageRequest.of(pageNum, pageSize));


        matchlist.toList().forEach(a -> {
            try {
                userProfiles.add(userService.getProfile(a.getId()));
            } catch (Exception e) {
                logger.error("Error fetching user profile for user id: " + a, e);
            }
        });

        userProfilePage.setContent(userProfiles);
        userProfilePage.setPageNum(matchlist.getNumber());
        userProfilePage.setPageSize(matchlist.getSize());
        userProfilePage.setNumberOfElements(matchlist.getNumberOfElements());
        userProfilePage.setTotalElements(matchlist.getTotalElements());
        userProfilePage.setTotalPages(matchlist.getTotalPages());

        return userProfilePage;


    }


}
