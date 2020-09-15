package com.wemeet.dating.service;

import com.wemeet.dating.dao.SwipeRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.SwipeType;
import com.wemeet.dating.model.request.SwipeRequest;
import com.wemeet.dating.model.response.SwipeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SwipeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SwipeRepository swipeRepository;
    private final UserService userService;

    @Autowired
    public SwipeService(SwipeRepository swipeRepository, UserService userService) {
        this.swipeRepository = swipeRepository;
        this.userService = userService;
    }


    public SwipeResponse swipe(SwipeRequest swipeRequest, User user) throws Exception {
        SwipeResponse response = new SwipeResponse();
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        User swipee = userService.findById(swipeRequest.getSwipeeId());
        if (swipee == null || swipee.getId() <= 0) {
            throw new BadRequestException("Swiped User does Not exist");
        }

        if (swipee.getId().equals(user.getId())) {
            throw new BadRequestException(("User cannot swipe itself"));
        }
        Swipe swipe = new Swipe();
        swipe.setType(swipeRequest.getType());
        swipe.setSwipee(swipee);
        swipe.setSwiper(user);
        try {
            swipe = swipeRepository.save(swipe);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("User has already been swiped by current user");
        }
        if (swipe.getType().equals(SwipeType.LIKE)) {
            Swipe counterSwipe = swipeRepository.findBySwiperAndSwipee(swipee, user);
            if (counterSwipe != null && counterSwipe.getType().equals(SwipeType.LIKE)) {
                response.setMatch(true);
            }
        }
        response.setSwipe(swipe);

        return response;
    }

    public Swipe findSwipe(Long id) {
        return swipeRepository.findById(id).orElse(null);
    }
}
