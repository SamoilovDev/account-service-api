package com.samoilov.dev.account.service.repository;

import com.samoilov.dev.account.service.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.samoilov.dev.account.service.service.UserAccountService.ACME_EMAIL_REGEX;

public interface UserAccountRepository extends JpaRepository<UserProfileEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 " +
            "THEN true ELSE false END " +
            "FROM UserProfileEntity u WHERE u.email = LOWER(:email)")
    boolean isEmailRegistered(@Param("email") String email);

    @Query("SELECT u FROM UserProfileEntity u " +
            "WHERE (u.email = LOWER(:emailOrName) AND :isEmail = true) " +
            "OR (u.name = :emailOrName AND u.lastName = :lastName AND :isEmail = false)")
    Optional<UserProfileEntity> findUserEntityByCustomCondition(
            @Param("emailOrName") String emailOrName,
            @Param("lastName") String lastName,
            @Param("isEmail") boolean isEmail);

    @Query("SELECT CASE WHEN COUNT(u) = 0 " +
            "THEN true ELSE false END " +
            "FROM UserProfileEntity u")
    boolean isDatabaseEmpty();


    default Optional<UserProfileEntity> findUserEntityByEmailOrFirstAndLastName(String emailOrName, String lastName) {
        boolean isEmail = Pattern.matches(ACME_EMAIL_REGEX, emailOrName);

        return Optional.ofNullable(lastName)
                .filter(ignored -> !isEmail)
                .map(ln -> this.findUserEntityByCustomCondition(emailOrName, ln, false))
                .orElse(this.findUserEntityByCustomCondition(emailOrName, null, true));
    }
}