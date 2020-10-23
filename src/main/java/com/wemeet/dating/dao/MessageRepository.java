package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Message;
import com.wemeet.dating.model.entity.Swipe;
import com.wemeet.dating.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;

public interface MessageRepository extends BaseRepository<Message, Long> {

    long countBySenderAndSentAtBetween(User sender, LocalDateTime dayStart, LocalDateTime dayEnd);

    @Query(
            value
                    = "SELECT * FROM message " +
                    " WHERE (sender_id = :senderId AND receiver_id = :receiverId) " +
                    " OR (sender_id = :receiverId AND receiver_id = :senderId) " +
                    " ORDER BY id DESC " ,
            countQuery
                    = "SELECT COUNT(*) FROM(SELECT * FROM message " +
                    " WHERE (sender_id = :senderId AND receiver_id = :receiverId) " +
                    " OR (sender_id = :receiverId AND receiver_id = :senderId) ) t " ,
            nativeQuery = true)
    Page<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByIdDesc(long senderId, long receiverId, Pageable pageable);


    Page<Message> findBySenderAndReceiver(User sender, User receiver, Pageable pageable);


}
