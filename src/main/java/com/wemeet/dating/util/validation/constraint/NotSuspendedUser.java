package com.wemeet.dating.util.validation.constraint;

import com.wemeet.dating.util.validation.validator.ActiveUserValidator;
import com.wemeet.dating.util.validation.validator.SuspendedUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SuspendedUserValidator.class)
@Documented
public @interface NotSuspendedUser {
    String message() default "Suspended User";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}