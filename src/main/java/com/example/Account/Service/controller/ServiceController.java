package com.example.Account.Service.controller;

import com.example.Account.Service.model.RoleChangeRequest;
import com.example.Account.Service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class ServiceController {

    @Autowired
    UserService userService;

    @PutMapping("/user/role")
    public ResponseEntity<?> changeRole(@Valid @RequestBody RoleChangeRequest request) {
        return userService.changeUserRole(request);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUsers() {
        return userService.getAllUsers();
    }

}
