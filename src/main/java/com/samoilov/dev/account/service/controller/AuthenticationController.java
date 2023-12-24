package com.samoilov.dev.account.service.controller;

import com.samoilov.dev.account.service.dto.ResponseUserStatusDto;
import com.samoilov.dev.account.service.dto.UserProfileDto;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import com.samoilov.dev.account.service.model.AccountPasswordDto;
import com.samoilov.dev.account.service.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserAccountService userDetailsService;

    @PostMapping("/signup")
    @Operation(summary = "Sign up a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid user information"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserProfileDto> signUp(@Valid @RequestBody UserProfileDto user) {
        return userDetailsService.addUser(user);
    }

    @PostMapping("/change-pass")
    @Operation(summary = "Change password for current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully changed password"),
        @ApiResponse(responseCode = "400", description = "Invalid new password or user info")
    })
    public ResponseEntity<ResponseUserStatusDto> changePass(
            @Valid @RequestBody AccountPasswordDto newPassword,
            @AuthenticationPrincipal UserProfileEntity user) {
        return userDetailsService.changePassword(newPassword.getPassword(), user.getEmail());
    }

}
