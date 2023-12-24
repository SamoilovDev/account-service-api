package com.samoilov.dev.account.service.validation;

import com.samoilov.dev.account.service.exception.PasswordAlreadyHackedException;
import com.samoilov.dev.account.service.exception.PasswordLengthException;
import com.samoilov.dev.account.service.exception.PasswordNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        List<String> breachedPasswords = this.getBreachedPasswords();

        return Optional.ofNullable(password)
                .filter(p -> {
                    if (breachedPasswords.contains(p)) {
                        throw new PasswordAlreadyHackedException();
                    }

                    if (p.length() < 12) {
                        throw new PasswordLengthException();
                    }

                    return !p.isBlank();
                })
                .map(ignored -> Boolean.TRUE)
                .orElseThrow(PasswordNotFoundException::new);
    }

    private List<String> getBreachedPasswords() {
        Query query = entityManager.createQuery("SELECT p.password FROM BreachedPasswordEntity p");
        return (List<String>) query.getResultList();
    }
}