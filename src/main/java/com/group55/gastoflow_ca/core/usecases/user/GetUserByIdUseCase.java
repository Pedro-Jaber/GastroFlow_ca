package com.group55.gastoflow_ca.core.usecases.user;

import java.util.UUID;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class GetUserByIdUseCase {

    private final IUserGateway userGateway;

    private GetUserByIdUseCase(IUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public static GetUserByIdUseCase create(IUserGateway userGateway) {
        return new GetUserByIdUseCase(userGateway);
    }

    public User run(UserToken userToken, UUID id) {

        boolean isSelf = userToken.getUserId().equals(id);

        if (isSelf) {
            AuthorizationChecker.requirePermission(userToken, Permission.READ_USER);
        } else {
            AuthorizationChecker.requirePermission(userToken, Permission.READ_ALL_USER);
        }

        return userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
    }
}
