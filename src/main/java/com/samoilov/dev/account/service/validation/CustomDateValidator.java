package com.samoilov.dev.account.service.validation;

import com.samoilov.dev.account.service.exception.NonExistentDateException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


public class CustomDateValidator implements ConstraintValidator<ValidDate, Date> {

    public static final String DATE_PATTERN = "MM-yyyy";

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(date)
                .map(d -> {
                    try {
                         return new SimpleDateFormat(DATE_PATTERN).parse(d.toString()).getTime() > 0;
                    } catch (ParseException ignored) {
                        return Boolean.FALSE;
                    }
                })
                .orElseThrow(NonExistentDateException::new);
    }
}


