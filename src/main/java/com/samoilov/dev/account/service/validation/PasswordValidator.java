package com.samoilov.dev.account.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private final List<String> breachedPasswords = List.of(
            "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember",
            "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    );

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(password)
                .filter(p -> {
                    if (breachedPasswords.contains(password)) {
                        throw new PasswordAlreadyHackedException();
                    } else if (password.length() < 12) {
                        throw new PasswordLengthException();
                    }

                    return !p.isBlank();
                })
                .map(ignored -> Boolean.TRUE)
                .orElseThrow(PasswordNotFoundException::new);
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password is required!")
class PasswordNotFoundException extends RuntimeException { }

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The password is in the hacker's database!")
class PasswordAlreadyHackedException extends RuntimeException { }

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password length must be 12 chars minimum!")
class PasswordLengthException extends RuntimeException { }

