package com.wemeet.dating.dao;

import com.wemeet.dating.model.entity.Transaction;
import com.wemeet.dating.model.entity.User;

public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    Transaction findByUserAndStatus(User user, String status);

    Transaction findByReference(String reference);
}
