package com.example.Account.Service.controller;

import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.service.UserService;
import com.example.Account.Service.model.NewPasswordModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    UserService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserEntity user) {
        return userDetailsService.addUser(user);
    }

    @PostMapping("/changepass")
    public ResponseEntity<?> changePass(@Valid @RequestBody NewPasswordModel newPassword,
                                        @AuthenticationPrincipal UserEntity user) {
        if (! newPassword.getPassword().isEmpty()) {
            return userDetailsService.changePassword(newPassword.getPassword(), user.getEmail());
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}

