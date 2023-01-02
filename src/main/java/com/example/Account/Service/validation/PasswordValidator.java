package com.example.Account.Service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private final List<String> breachedPasswords = List.of(
            "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember",
            "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
    );

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.equals(password, null) || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required!");
        } else if (breachedPasswords.contains(password)) {
            throw new PasswordAlreadyHackedException();
        } else if (password.length() < 12) {
            throw new PasswordLengthException();
        } else return true;
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The password is in the hacker's database!")
@NoArgsConstructor
class PasswordAlreadyHackedException extends RuntimeException {
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password length must be 12 chars minimum!")
class PasswordLengthException extends RuntimeException {
}

