package com.example.Account.Service.service;

import com.example.Account.Service.config.ApplicationConfiguration;
import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.model.Role;
import com.example.Account.Service.model.RoleChangeRequest;
import com.example.Account.Service.repository.UserRepo;
import lombok.NoArgsConstructor;
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
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApplicationConfiguration configuration;

    public ResponseEntity<?> addUser(UserEntity user) {
        if (userRepo.findUserEntityByEmailIgnoreCase(user.getEmail()).isEmpty()) {
            user.setPassword(configuration.getEncoder().encode(user.getPassword()));

            user.getRoles().add(
                    userRepo.findById(1L).isEmpty()
                    ? Role.ADMINISTRATOR
                    : Role.USER
            );

            userRepo.save(user);

            return ResponseEntity.ok(user);
        } else {
            throw new UserExistException();
        }
    }

    public ResponseEntity<?> changeUserRole(RoleChangeRequest request) {
        UserEntity user = (UserEntity) loadUserByUsername(request.getUser());
        checkRoleChangeRequest(user, request);

        boolean grant = request.getOperation().equalsIgnoreCase("GRANT")
                ? user.getRoles().add(Role.valueOf(request.getRole()))
                : user.getRoles().remove(Role.valueOf(request.getRole()));

        userRepo.save(user);
        return ResponseEntity.ok(user);
    }

    private void checkRoleChangeRequest(UserEntity user, RoleChangeRequest request) {

         if (user.getRoles().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        } else if (request.getRole().equalsIgnoreCase("ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else if (user.getRoles().stream().anyMatch(role ->
                        ! role.getRoleType().equals(Role.valueOf(request.getRole()).getRoleType()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

         if (request.getOperation().equalsIgnoreCase("GRANT") &&
                 user.getRoles().contains(Role.valueOf(request.getRole()))) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already had this role!");
         } else if (request.getOperation().equalsIgnoreCase("REMOVE")
                 && user.getRoles().stream().noneMatch(role -> role.toString().equals(request.getRole()))) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
         }
    }

    public ResponseEntity<?> getAllUsers() {
        List<UserEntity> users = new ArrayList<>();
        userRepo.findAll().forEach(users::add);
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<?> deleteUser(String email) {
        if (userRepo.findUserEntityByEmailIgnoreCase(email).isPresent()) {
            UserEntity user = userRepo.findUserEntityByEmailIgnoreCase(email).get();

            if (user.getRoles().stream().anyMatch(role -> role.equals(Role.ADMINISTRATOR))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }

            userRepo.delete(user);
            return ResponseEntity.ok(Map.of("user", email, "status", "Deleted successfully!"));
        } else throw new UserNotFoundException();
    }

    public ResponseEntity<?> changePassword(String newPassword, String email) {
        UserEntity user = (UserEntity) loadUserByUsername(email);

        if (configuration.getEncoder().matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible to change the same passwords!");
        }

        user.setPassword(configuration.getEncoder().encode(newPassword));
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("email", user.getEmail(),
                "status", "The password has been updated successfully"));

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return username.matches("^[a-zA-Z0-9_.+-]+@acme\\.com$")
                ? userRepo
                .findUserEntityByEmailIgnoreCase(username)
                .orElseThrow(UserNotFoundException::new)
                : userRepo
                .findUserEntityByNameAndLastName(username.split("\\s")[0],
                        username.split("\\s").length > 1 ? username.split("\\s")[1] : "")
                .orElseThrow(UserNotFoundException::new);
    }

}




@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found!")
@NoArgsConstructor
class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}




@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User exist!")
@NoArgsConstructor
class UserExistException extends RuntimeException {
    public UserExistException(String message) {
        super(message);
    }
}
