package com.samoilov.dev.account.service.mapper;

import com.samoilov.dev.account.service.dto.UserProfileDto;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class UserCredentialsMapper {

    public UserProfileEntity mapUserProfileDtoToEntity(UserProfileDto userProfileDto) {
        return Optional.ofNullable(userProfileDto)
                .map(upd -> UserProfileEntity.builder()
                        .id(upd.getId())
                        .name(upd.getName())
                        .lastName(upd.getLastName())
                        .email(upd.getEmail())
                        .password(upd.getPassword())
                        .payments(new ArrayList<>())
                        .roles(upd.getRoles())
                        .build())
                .orElse(null);
    }

    public UserProfileDto mapUserProfileEntityToDto(UserProfileEntity userProfileEntity) {
        return Optional.ofNullable(userProfileEntity)
                .map(upe -> UserProfileDto.builder()
                        .id(upe.getId())
                        .name(upe.getName())
                        .lastName(upe.getLastName())
                        .email(upe.getEmail())
                        .password(upe.getPassword())
                        .roles(upe.getRoles())
                        .build())
                .orElse(null);
    }

}
