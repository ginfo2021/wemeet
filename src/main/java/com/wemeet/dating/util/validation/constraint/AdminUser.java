package com.wemeet.dating.util.validation.constraint;


import com.wemeet.dating.util.validation.validator.ActiveUserValidator;
import com.wemeet.dating.util.validation.validator.AdminUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdminUserValidator.class)
@Documented
public @interface AdminUser {
    String message() default "User Not Admin";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
