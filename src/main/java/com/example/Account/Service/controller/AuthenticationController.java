package com.example.Account.Service.controller;

import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.service.UserService;
import com.example.Account.Service.model.NewPasswordModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> signUp(@Valid @RequestBody UserEntity user) {
        return userDetailsService.addUser(user);
    }

    @PostMapping("/change-pass")
    public ResponseEntity<Map<String, String>> changePass(@Valid @RequestBody NewPasswordModel newPassword,
                                                          @AuthenticationPrincipal UserEntity user) {
        return userDetailsService.changePassword(newPassword.getPassword(), user.getEmail());
    }

}

