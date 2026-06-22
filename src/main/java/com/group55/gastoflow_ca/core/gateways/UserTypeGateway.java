package com.group55.gastoflow_ca.core.gateways;

import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class UserTypeGateway implements IUserTypeGateway {

    private final IUserTypeDataSource dataStorageSource;

    private UserTypeGateway(IUserTypeDataSource dataSource) {
        this.dataStorageSource = dataSource;
    }

    public static UserTypeGateway create(IUserTypeDataSource dataSource) {
        return new UserTypeGateway(dataSource);
    }

    @Override
    public UserType findById(UUID id) {
        final UserTypeDTO userTypeDTO = this.dataStorageSource.findById(id);

        if (userTypeDTO == null) {
            throw new UserTypeNotFoundException("UserType with id" + id + " not found.");
        }

        return UserType.create(
                userTypeDTO.id(),
                userTypeDTO.name(),
                userTypeDTO.permissions());

    }
}
