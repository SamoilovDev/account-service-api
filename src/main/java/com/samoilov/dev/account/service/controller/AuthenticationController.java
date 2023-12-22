package com.samoilov.dev.account.service.controller;

import com.samoilov.dev.account.service.entity.UserEntity;
import com.samoilov.dev.account.service.model.NewPasswordModel;
import com.samoilov.dev.account.service.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserAccountService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> signUp(@Valid @RequestBody UserEntity user) {
        return userDetailsService.addUser(user);
    }

    @PostMapping("/change-pass")
    public ResponseEntity<Map<String, String>> changePass(
            @Valid @RequestBody NewPasswordModel newPassword,
            @AuthenticationPrincipal UserEntity user) {
        return userDetailsService.changePassword(newPassword.getPassword(), user.getEmail());
    }

}

