package com.group55.gastoflow_ca.core.usecases.userType;

import java.util.UUID;

import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class DeleteUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;

    private DeleteUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public static DeleteUserTypeUseCase create(IUserTypeGateway userTypeGateway) {
        return new DeleteUserTypeUseCase(userTypeGateway);
    }

    public void run(UUID id) {
        this.userTypeGateway.deleteById(id);
    }

}
