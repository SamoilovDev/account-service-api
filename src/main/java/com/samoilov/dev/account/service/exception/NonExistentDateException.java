package com.samoilov.dev.account.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Date must exists!")
public class NonExistentDateException extends RuntimeException {

}