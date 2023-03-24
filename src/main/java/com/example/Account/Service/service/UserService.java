package com.example.Account.Service.service;

import com.example.Account.Service.config.ApplicationConfiguration;
import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.model.Role;
import com.example.Account.Service.model.RoleChangeRequest;
import com.example.Account.Service.repository.UserRepo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApplicationConfiguration configuration;

    public ResponseEntity<UserEntity> addUser(UserEntity user) {
        if (userRepo.findUserEntityByEmailIgnoreCase(user.getEmail()).isEmpty()) {
            user.setPassword(configuration.getEncoder().encode(user.getPassword()));

            log.info("Check is user the first and set role");
            user.getRoles().add(
                    userRepo.findById(1L).isEmpty()
                    ? Role.ADMINISTRATOR
                    : Role.USER
            );

            userRepo.save(user);
            log.info("Save new user's entity to repo and send answer");
            return ResponseEntity.ok(user);
        } else {
            log.info("Create user exist exception");
            throw new UserExistException();
        }
    }

    public ResponseEntity<UserEntity> changeUserRole(RoleChangeRequest request) {
        UserEntity user = checkRoleChangeRequest((UserEntity) loadUserByUsername(request.getUser()), request);

        log.info("Check and do command");
        if (
                request.getOperation().equalsIgnoreCase("GRANT")
                        ? user.getRoles().add(Role.valueOf(request.getRole()))
                        : user.getRoles().remove(Role.valueOf(request.getRole()))
        ) {
            log.info("Save changes and send answer");
            userRepo.save(user);
        }

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = (List<UserEntity>) userRepo.findAll();
        log.info("Add all users from repository to list and send answer");
        return ResponseEntity.ok(users.isEmpty() ? new ArrayList<>() : users);
    }

    public ResponseEntity<Map<String, String>> deleteUser(String email) {
        UserEntity user = userRepo.findUserEntityByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);

        if (user.getRoles().contains(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else {
            userRepo.delete(user);
            return ResponseEntity.ok(Map.of("user", email, "status", "Deleted successfully!"));
        }
    }

    public ResponseEntity<Map<String, String>> changePassword(String newPassword, String email) {
        UserEntity user = (UserEntity) loadUserByUsername(email);

        if (configuration.getEncoder().matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible to change the same passwords!");
        }

        user.setPassword(configuration.getEncoder().encode(newPassword));
        userRepo.save(user);
        return ResponseEntity.ok(
                Map.of(
                        "email", user.getEmail(),
                        "status", "The password has been updated successfully"
                )
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return username.matches("^[a-zA-Z0-9_.+-]+@acme\\.com$")
                ? userRepo.findUserEntityByEmailIgnoreCase(username)
                    .orElseThrow(UserNotFoundException::new)
                : userRepo.findUserEntityByNameAndLastName(
                        username.split("\\s")[0],
                        username.split("\\s").length > 1 ? username.split("\\s")[1] : ""
                )
                .orElseThrow(UserNotFoundException::new);
    }

    private UserEntity checkRoleChangeRequest(UserEntity user, RoleChangeRequest request) {
        log.info("Check role change request");
        if (user.getRoles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        } else if (request.getRole().equalsIgnoreCase("ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else if (
                user.getRoles()
                        .stream()
                        .anyMatch(role -> ! role.getRoleType().equals(Role.valueOf(request.getRole()).getRoleType()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        } else if (request.getOperation().equalsIgnoreCase("GRANT") &&
                user.getRoles().contains(Role.valueOf(request.getRole()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already had this role!");
        } else if (
                request.getOperation().equalsIgnoreCase("REMOVE")
                        && user.getRoles().stream().noneMatch(role -> role.toString().equals(request.getRole()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        } else return user;
    }

}




@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found!")
@NoArgsConstructor
class UserNotFoundException extends RuntimeException { }




@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "User exist!")
@NoArgsConstructor
class UserExistException extends RuntimeException { }
