package com.example.Account.Service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class CustomDateValidator implements ConstraintValidator<ValidDate, Date> {

    public static final String DATE_PATTERN = "MM-yyyy";

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.equals(date, null)) {
            throw new NonExistentDateException();
        }
        try {
            new SimpleDateFormat(DATE_PATTERN).parse(date.toString());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Date must exists!")
@NoArgsConstructor
class NonExistentDateException extends RuntimeException {
}
