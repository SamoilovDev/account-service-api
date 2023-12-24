package com.samoilov.dev.account.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.samoilov.dev.account.service.entity.PaymentEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link PaymentEntity}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonAlias(value = "user_email")
    @NotBlank(message = "Email is required")
    @Email(regexp = "^[-.+\\w]+@acme\\.com$", message = "Domain should be @acme.com")
    String userEmail;

    Date period;

    @NotNull(message = "Salary num is required!")
    @Min(message = "Salary can not be less than zero!", value = 0)
    Double salary;

    UserProfileDto user;

}