package com.example.Account.Service.model;

import com.example.Account.Service.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class NewPasswordModel {

    @ValidPassword
    @JsonAlias("new_password")
    private String password;

}
