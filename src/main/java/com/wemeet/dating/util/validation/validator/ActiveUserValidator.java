package com.wemeet.dating.util.validation.validator;

import com.wemeet.dating.service.UserVerificationService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@Component
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ActiveUserValidator implements ConstraintValidator<ActiveUser, Object> {

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        ActiveUserValidator.applicationContext = applicationContext;
    }

    @Autowired
    UserVerificationService userVerificationService;

    @Override
    public void initialize(ActiveUser activeUser) {
        userVerificationService = (UserVerificationService) applicationContext.getBean("userVerificationService");
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return userVerificationService.isActiveUser();
    }
}
