package com.example.Account.Service.controller;

import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.model.RoleChangeRequest;
import com.example.Account.Service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class ServiceController {

    @Autowired
    private UserService userService;

    @PutMapping("/user/role")
    public ResponseEntity<UserEntity> changeRole(@Valid @RequestBody RoleChangeRequest request) {
        return userService.changeUserRole(request);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserEntity>> getUsers() {
        return userService.getAllUsers();
    }

}
