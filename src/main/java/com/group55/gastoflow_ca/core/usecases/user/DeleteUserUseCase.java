package com.group55.gastoflow_ca.core.usecases.user;

import java.util.UUID;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class DeleteUserUseCase {

    private final IUserGateway userGateway;

    private DeleteUserUseCase(IUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public static DeleteUserUseCase create(IUserGateway userGateway) {
        return new DeleteUserUseCase(userGateway);
    }

    public void run(UserToken userToken, UUID id) {

        boolean isSelf = userToken.getUserId().equals(id);

        if (isSelf) {
            AuthorizationChecker.requirePermission(userToken, Permission.DELETE_USER);
        } else {
            AuthorizationChecker.requirePermission(userToken, Permission.DELETE_ALL_USER);
        }

        this.userGateway.deleteById(id);
    }
}
