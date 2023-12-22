package com.samoilov.dev.account.service.repository;

import com.samoilov.dev.account.service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByEmailIgnoreCase(String email);

    Optional<UserEntity> findUserEntityByNameAndLastName(String name, String lastName);

}
