package com.dasi.common.annotation;

import com.dasi.web.validator.AtLeastOneContactValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneContactValidator.class)
public @interface AtLeastOneContact {
    String message() default "手机号、邮箱至少一个";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}