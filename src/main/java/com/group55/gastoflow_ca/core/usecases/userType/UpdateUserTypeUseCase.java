package com.group55.gastoflow_ca.core.usecases.userType;

import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.UpdateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class UpdateUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;

    private UpdateUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public static UpdateUserTypeUseCase create(IUserTypeGateway userTypeGateway) {
        return new UpdateUserTypeUseCase(userTypeGateway);
    }

    public UserType run(UUID id, UpdateUserTypeInputDataDTO input) {

        // Verify if usertype exists
        var existingUserType = this.userTypeGateway.findById(id)
                .orElseThrow(() -> new UserTypeNotFoundException(
                        "UserType with id " + id + " not found."));

        if (input.name() != null && !input.name().isBlank()) {
            existingUserType.setName(input.name());
        }

        if (input.permissions() != null) {
            existingUserType.setPermissions(input.permissions());
        }

        var savedUserType = this.userTypeGateway.updateUserType(existingUserType);

        return savedUserType;
    }

}
