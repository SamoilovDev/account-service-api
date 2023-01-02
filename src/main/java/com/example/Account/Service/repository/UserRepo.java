package com.example.Account.Service.repository;

import com.example.Account.Service.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntityByEmailIgnoreCase(String email);

    Optional<UserEntity> findUserEntityByNameAndLastName(String name, String lastName);
}
