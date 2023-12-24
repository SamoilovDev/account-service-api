package com.samoilov.dev.account.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Password length must be 12 chars minimum!")
public class PasswordLengthException extends RuntimeException {

}

