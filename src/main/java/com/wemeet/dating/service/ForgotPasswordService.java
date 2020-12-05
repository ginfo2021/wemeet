package com.wemeet.dating.service;

import com.wemeet.dating.dao.ForgotPasswordRepository;
import com.wemeet.dating.model.entity.ForgotPassword;
import com.wemeet.dating.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordService {

    private final ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository) {
        this.forgotPasswordRepository = forgotPasswordRepository;
    }

    public ForgotPassword findByToken(String token){
        return forgotPasswordRepository.findByToken(token);
    }

    public ForgotPassword saveEntity(ForgotPassword forgotPassword){
        return forgotPasswordRepository.save(forgotPassword);
    }

    public ForgotPassword getByUser(User user) {
        return forgotPasswordRepository.findTop1ByUserOrderByIdDesc(user);
    }
}
