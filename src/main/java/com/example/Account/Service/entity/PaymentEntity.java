package com.example.Account.Service.entity;

import com.example.Account.Service.validation.ValidDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(regexp = "^[-.+\\w]+@acme\\.com$", message = "Domain should be @acme.com")
    @JsonAlias(value = "user_email")
    private String userEmail;

    @ValidDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM-yyyy")
    @Temporal(TemporalType.DATE)
    private Date period;

    @NotNull(message = "Salary num is required!")
    @Min(value = 0, message = "Salary can not be less than zero!")
    private Double salary;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
