package com.group55.gastoflow_ca.core.usecases.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class UpdateUserUseCase {

    private final IUserGateway userGateway;
    private final IUserTypeGateway userTypeGateway;

    private final IPasswordHasher passwordHasher;

    private UpdateUserUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway,
            IPasswordHasher passwordHasher) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.passwordHasher = passwordHasher;
    }

    public static UpdateUserUseCase create(IUserGateway userGateway, IUserTypeGateway userTypeGateway,
            IPasswordHasher passwordHasher) {
        return new UpdateUserUseCase(userGateway, userTypeGateway, passwordHasher);
    }

    public User run(UserToken userToken, UUID id, UpdateUserInputDataDTO input) {

        boolean isSelf = userToken.getUserId().equals(id);

        if (isSelf) {
            AuthorizationChecker.requirePermission(userToken, Permission.EDIT_USER);
        } else {
            AuthorizationChecker.requirePermission(userToken, Permission.EDIT_ALL_USER);
        }

        // Verify if user exists
        User existingUser = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));

        if (input.name() != null && !input.name().isBlank()) {
            existingUser.setName(input.name());
        }

        if (input.emailAddress() != null && !input.emailAddress().isBlank()) {
            existingUser.setEmailAddress(input.emailAddress());
        }

        if (input.login() != null && !input.login().isBlank()) {
            existingUser.setLogin(input.login());
        }

        if (input.password() != null && !input.password().isBlank()) {
            final String encondedPassword = this.passwordHasher.encode(input.password());
            existingUser.setPassword(encondedPassword);
        }

        if (input.userTypeId() != null) {
            UserType userType = userTypeGateway.findById(input.userTypeId()).orElseThrow(
                    () -> new UserTypeNotFoundException("UserType with id " + input.userTypeId() + " not found."));

            existingUser.setUserType(userType);
        }

        existingUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userGateway.updateUser(existingUser);

        return savedUser;
    }
}
