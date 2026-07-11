package com.group55.gastoflow_ca.core.usecases.userType;

import java.util.UUID;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
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

    public UserType run(UserToken userToken, UUID id) {

        AuthorizationChecker.requirePermission(userToken, Permission.READ_USERTYPE);

        return userTypeGateway.findById(id)
                .orElseThrow(() -> new UserTypeNotFoundException("UserType with id " + id + " not found."));
    }

}
