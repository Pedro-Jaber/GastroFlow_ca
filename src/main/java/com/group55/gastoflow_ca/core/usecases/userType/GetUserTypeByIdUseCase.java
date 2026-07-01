package com.group55.gastoflow_ca.core.usecases.userType;

import java.util.UUID;

import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class GetUserTypeByIdUseCase {

    private final IUserTypeGateway userTypeGateway;

    private GetUserTypeByIdUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public static GetUserTypeByIdUseCase create(IUserTypeGateway userTypeGateway) {
        return new GetUserTypeByIdUseCase(userTypeGateway);
    }

    public UserType run(UUID id) {
        return userTypeGateway.findById(id)
                .orElseThrow(() -> new UserTypeNotFoundException("UserType with id " + id + " not found."));
    }

}
