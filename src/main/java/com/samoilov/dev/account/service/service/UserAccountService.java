package com.samoilov.dev.account.service.service;

import com.samoilov.dev.account.service.dto.ResponseUserStatusDto;
import com.samoilov.dev.account.service.dto.UserProfileDto;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import com.samoilov.dev.account.service.exception.UserAlreadyExistsException;
import com.samoilov.dev.account.service.exception.UserNotFoundException;
import com.samoilov.dev.account.service.mapper.UserCredentialsMapper;
import com.samoilov.dev.account.service.model.OperationType;
import com.samoilov.dev.account.service.model.RoleChangeRequest;
import com.samoilov.dev.account.service.model.RoleType;
import com.samoilov.dev.account.service.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    private final UserCredentialsMapper userCredentialsMapper;

    private final PasswordEncoder passwordEncoder;

    public static final String ACME_EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@acme\\.com$";

    @Transactional
    public ResponseEntity<UserProfileDto> addUser(UserProfileDto user) {
        if (userAccountRepository.isEmailRegistered(user.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        RoleType roleOfNewUser = userAccountRepository.isDatabaseEmpty()
                ? RoleType.ADMINISTRATOR
                : RoleType.USER;

        user.getRoles().add(roleOfNewUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(
                userAccountRepository
                        .save(userCredentialsMapper.mapUserProfileDtoToEntity(user))
                        .getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Transactional
    public ResponseEntity<UserProfileDto> changeUserRole(RoleChangeRequest request) {
        UserProfileEntity user = (UserProfileEntity) this.loadUserByUsername(request.getUser());

        this.checkRoleChangeRequest(user, request);

        if (request.getOperation() == OperationType.GRANT) {
            user.getRoles().add(RoleType.valueOf(request.getRole()));
        } else {
            user.getRoles().remove(RoleType.valueOf(request.getRole()));
        }

        return ResponseEntity.ok(
                userCredentialsMapper.mapUserProfileEntityToDto(userAccountRepository.save(user))
        );
    }

    public ResponseEntity<List<UserProfileEntity>> getAllUsers() {
        return ResponseEntity.ok(userAccountRepository.findAll());
    }

    public ResponseEntity<ResponseUserStatusDto> deleteUser(String email) {
        return userAccountRepository.findUserEntityByEmailOrFirstAndLastName(email, null)
                .map(user -> {
                    if (user.getRoles().contains(RoleType.ADMINISTRATOR)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible to remove ADMINISTRATOR role!");
                    }

                    userAccountRepository.delete(user);

                    return ResponseEntity.ok((ResponseUserStatusDto) ResponseUserStatusDto.builder()
                            .status("Deleted successfully!")
                            .email(email)
                            .build());
                })
                .orElseThrow(UserNotFoundException::new);
    }

    public ResponseEntity<ResponseUserStatusDto> changePassword(CharSequence newPassword, String email) {
        UserProfileEntity user = (UserProfileEntity) this.loadUserByUsername(email);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible to change the same passwords!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userAccountRepository.save(user);

        return ResponseEntity.ok(ResponseUserStatusDto.builder()
                .status("The password has been updated successfully")
                .email(user.getEmail())
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        String[] splitUsername = username.split("\\s");
        String lastName = splitUsername.length > 1
                ? splitUsername[1]
                : EMPTY;

        return userAccountRepository
                .findUserEntityByEmailOrFirstAndLastName(splitUsername[0], lastName)
                .orElseThrow(UserNotFoundException::new);
    }

    private void checkRoleChangeRequest(UserProfileEntity user, RoleChangeRequest request) {
        this.checkUserRoles(user);
        this.disallowRoleChangeIfRequestForAdmin(request);
        this.checkCombinationOfRoles(user, request);
        this.checkRoleGrant(user, request);
        this.checkRoleRemove(user, request);
    }

    private void checkUserRoles(UserProfileEntity user) {
        if (user.getRoles().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
    }

    private void disallowRoleChangeIfRequestForAdmin(RoleChangeRequest request) {
        if (request.getRole().equalsIgnoreCase("ADMINISTRATOR"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
    }

    private void checkCombinationOfRoles(UserProfileEntity user, RoleChangeRequest request) {
        if (user.getRoles()
                .stream()
                .anyMatch(role -> !role.getRoleType().equals(RoleType.valueOf(request.getRole()).getRoleType())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
    }

    private void checkRoleGrant(UserProfileEntity user, RoleChangeRequest request) {
        if (request.getOperation() == OperationType.GRANT && containsRole(user, request))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already had this role!");
    }

    private void checkRoleRemove(UserProfileEntity user, RoleChangeRequest request) {
        if (request.getOperation() == OperationType.REMOVE
                && user.getRoles().stream().noneMatch(role -> role.toString().equals(request.getRole())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
    }

    private boolean containsRole(UserProfileEntity user, RoleChangeRequest request) {
        return user.getRoles().contains(RoleType.valueOf(request.getRole()));
    }
}
