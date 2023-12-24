package com.samoilov.dev.account.service.controller;

import com.samoilov.dev.account.service.dto.ResponseUserStatusDto;
import com.samoilov.dev.account.service.dto.UserProfileDto;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import com.samoilov.dev.account.service.model.RoleChangeRequest;
import com.samoilov.dev.account.service.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ServiceController {

    private final UserAccountService userAccountService;

    @PutMapping("/user/role")
    public ResponseEntity<UserProfileDto> changeRole(@Valid @RequestBody RoleChangeRequest request) {
        return userAccountService.changeUserRole(request);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<ResponseUserStatusDto> deleteUser(@PathVariable String email) {
        return userAccountService.deleteUser(email);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserProfileEntity>> getUsers() {
        return userAccountService.getAllUsers();
    }

}
