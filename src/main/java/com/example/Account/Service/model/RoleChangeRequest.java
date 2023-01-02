package com.example.Account.Service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleChangeRequest {

    @NotBlank(message = "User is required!")
    private String user;

    @NotBlank(message = "You must specify role!")
    private String role;

    @Pattern(regexp = "^(GRANT|REMOVE)$", message = "This operation doesn't exist!")
    private String operation;
}
