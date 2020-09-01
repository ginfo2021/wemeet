package com.wemeet.dating.util.validation.constraint;


import com.wemeet.dating.util.validation.validator.ActiveUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ActiveUserValidator.class)
@Documented
public @interface ActiveUser {
    String message() default "Inactive User";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
