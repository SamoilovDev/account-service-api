package com.samoilov.dev.account.service.model;

import com.samoilov.dev.account.service.validation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class AccountPasswordDto {

    @ValidPassword
    @JsonAlias({"new_password", "password"})
    private CharSequence password;

}
