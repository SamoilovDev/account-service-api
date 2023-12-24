package com.samoilov.dev.account.service.dto;

import com.samoilov.dev.account.service.entity.UserProfileEntity;
import com.samoilov.dev.account.service.model.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link UserProfileEntity}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Lastname is required")
    private String lastName;

    @Email(message = "Domain should be @acme.com", regexp = "^[a-zA-Z0-9_.+-]+@acme\\.com$")
    @NotBlank(message = "Email is required")
    private String email;

    @ToString.Exclude
    private String password;

    private List<RoleType> roles;

}