package com.example.Account.Service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {

    String message() default "Invalid date format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
