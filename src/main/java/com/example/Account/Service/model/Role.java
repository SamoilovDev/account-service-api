package com.example.Account.Service.model;

import lombok.Getter;

@Getter
public enum Role {
    USER("business"),

    ACCOUNTANT("business"),

    ADMINISTRATOR("administrative");

    private final String roleType;

    Role(String roleType) {
        this.roleType = roleType;
    }
}
