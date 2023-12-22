package com.samoilov.dev.account.service.entity;

import com.samoilov.dev.account.service.validation.ValidDate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "employee")
@Getter @Setter
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonAlias(value = "user_email")
    @NotBlank(message = "Email is required")
    @Email(regexp = "^[-.+\\w]+@acme\\.com$", message = "Domain should be @acme.com")
    private String userEmail;

    @ValidDate
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "MM-yyyy")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date period;

    @NotNull(message = "Salary num is required!")
    @Min(value = 0, message = "Salary can not be less than zero!")
    private Double salary;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
