package com.samoilov.dev.account.service.service;

import com.samoilov.dev.account.service.entity.UserEntity;
import com.samoilov.dev.account.service.exception.UserExistException;
import com.samoilov.dev.account.service.exception.UserNotFoundException;
import com.samoilov.dev.account.service.model.OperationType;
import com.samoilov.dev.account.service.model.RoleType;
import com.samoilov.dev.account.service.model.RoleChangeRequest;
import com.samoilov.dev.account.service.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<UserEntity> addUser(UserEntity user) {
        if (userAccountRepository.findUserEntityByEmailIgnoreCase(user.getEmail()).isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.getRoles().add(
                    userAccountRepository.findById(1L).isEmpty()
                            ? RoleType.ADMINISTRATOR
                            : RoleType.USER
            );

            userAccountRepository.save(user);

            return ResponseEntity.ok(user);
        } else {
            throw new UserExistException();
        }
    }

    public ResponseEntity<UserEntity> changeUserRole(RoleChangeRequest request) {
        UserEntity user = this.checkRoleChangeRequest((UserEntity) this.loadUserByUsername(request.getUser()), request);
        boolean userDataChanged = request.getOperation() == OperationType.GRANT
                ? user.getRoles().add(RoleType.valueOf(request.getRole()))
                : user.getRoles().remove(RoleType.valueOf(request.getRole()));

        if (userDataChanged) {
            userAccountRepository.save(user);
        }

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userAccountRepository.findAll());
    }

    public ResponseEntity<Map<String, String>> deleteUser(String email) {
        return userAccountRepository.findUserEntityByEmailIgnoreCase(email)
                .map(user -> {
                    if (user.getRoles().contains(RoleType.ADMINISTRATOR)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible to remove ADMINISTRATOR role!");
                    }

                    userAccountRepository.delete(user);
                    return ResponseEntity.ok(Map.of("user", email, "status", "Deleted successfully!"));
                })
                .orElseThrow(UserNotFoundException::new);
    }

    public ResponseEntity<Map<String, String>> changePassword(String newPassword, String email) {
        UserEntity user = (UserEntity) loadUserByUsername(email);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible to change the same passwords!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
        return ResponseEntity.ok(
                Map.of(
                        "email", user.getEmail(),
                        "status", "The password has been updated successfully"
                )
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        return username.matches("^[a-zA-Z0-9_.+-]+@acme\\.com$")
                ? userAccountRepository.findUserEntityByEmailIgnoreCase(username)
                    .orElseThrow(UserNotFoundException::new)
                : userAccountRepository.findUserEntityByNameAndLastName(
                        username.split("\\s")[0],
                        username.split("\\s").length > 1 ? username.split("\\s")[1] : ""
                )
                .orElseThrow(UserNotFoundException::new);
    }

    private UserEntity checkRoleChangeRequest(UserEntity user, RoleChangeRequest request) {
        if (user.getRoles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        } else if (request.getRole().equalsIgnoreCase("ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else if (
                user.getRoles()
                        .stream()
                        .anyMatch(role -> !role.getRoleType().equals(RoleType.valueOf(request.getRole()).getRoleType()))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        } else if (request.getOperation() == OperationType.GRANT && user.getRoles().contains(RoleType.valueOf(request.getRole()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already had this role!");
        } else if (request.getOperation() == OperationType.REMOVE
                && user.getRoles().stream().noneMatch(role -> role.toString().equals(request.getRole()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        } else return user;
    }

}
