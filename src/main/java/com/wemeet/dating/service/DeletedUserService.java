package com.wemeet.dating.service;

import com.wemeet.dating.dao.DeletedUserRepository;
import com.wemeet.dating.model.entity.DeletedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeletedUserService {

    private DeletedUserRepository deletedUserRepository;

    @Autowired
    public DeletedUserService(DeletedUserRepository deletedUserRepository) {
        this.deletedUserRepository = deletedUserRepository;
    }

    public DeletedUser createDeletedUser(DeletedUser deletedUser) {
        return deletedUserRepository.save(deletedUser);
    }
}
