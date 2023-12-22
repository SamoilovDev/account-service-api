package com.samoilov.dev.account.service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    USER("business"),

    ACCOUNTANT("business"),

    ADMINISTRATOR("administrative");

    private final String roleType;

}
