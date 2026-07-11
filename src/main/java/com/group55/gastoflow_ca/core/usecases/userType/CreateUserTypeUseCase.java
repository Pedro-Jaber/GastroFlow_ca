package com.group55.gastoflow_ca.core.usecases.userType;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.UserTypeAlreadyExistsException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class CreateUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;

    private CreateUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public static CreateUserTypeUseCase create(IUserTypeGateway userTypeGateway) {
        return new CreateUserTypeUseCase(userTypeGateway);
    }

    public UserType run(UserToken userToken, CreateUserTypeInputDataDTO input) {

        AuthorizationChecker.requirePermission(userToken, Permission.CREATE_USERTYPE);

        // Verify if usertype already exists
        this.userTypeGateway.findByName(input.name())
                .ifPresent(userType -> {
                    throw new UserTypeAlreadyExistsException(
                            "UserType with name " + input.name() + " already exists.");
                });

        var newUserType = UserType.create(input.name(), input.permissions());

        var userType = this.userTypeGateway.saveNewUserType(newUserType);

        return userType;
    }

}
