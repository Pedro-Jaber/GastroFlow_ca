package com.group55.gastoflow_ca.core.usecases.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class UpdateUserUseCase {

    private final IUserGateway userGateway;
    private final IUserTypeGateway userTypeGateway;

    private UpdateUserUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
    }

    public static UpdateUserUseCase create(IUserGateway userGateway, IUserTypeGateway userTypeGateway) {
        return new UpdateUserUseCase(userGateway, userTypeGateway);
    }

    public User run(UUID id, UpdateUserInputDataDTO input) {

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
