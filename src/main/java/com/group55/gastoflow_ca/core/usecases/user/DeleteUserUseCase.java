package com.group55.gastoflow_ca.core.usecases.user;

import java.util.UUID;

import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class DeleteUserUseCase {

    private final IUserGateway userGateway;

    private DeleteUserUseCase(IUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public static DeleteUserUseCase create(IUserGateway userGateway) {
        return new DeleteUserUseCase(userGateway);
    }

    public void run(UUID id) {
        this.userGateway.deleteById(id);
    }
}
