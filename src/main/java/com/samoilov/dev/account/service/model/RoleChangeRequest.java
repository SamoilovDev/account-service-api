package com.samoilov.dev.account.service.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleChangeRequest {

    @NotBlank(message = "User is required!")
    private String user;

    @NotBlank(message = "You must specify role!")
    private String role;

    private OperationType operation;
}
