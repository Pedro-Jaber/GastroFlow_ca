package com.group55.gastoflow_ca.core.gateways;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
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
    public UserType saveNewUserType(UserType newUserType) {
        final CreateUserTypeInputDataDTO newUserTypeDTO = new CreateUserTypeInputDataDTO(
                newUserType.getName(),
                newUserType.getPermissions());

        final UserTypeDTO createdUserType = this.dataStorageSource.saveNewUserType(newUserTypeDTO);

        return UserType.create(
                createdUserType.id(),
                createdUserType.name(),
                createdUserType.permissions());
    }

    // TODO tranformar em Optional e passar a tratativa de erro para o gateway
    @Override
    public UserType findById(UUID id) {
        final UserTypeDTO userTypeDTO = this.dataStorageSource.findById(id);

        if (userTypeDTO == null) {
            throw new UserTypeNotFoundException("UserType with id " + id + " not found.");
        }

        return UserType.create(
                userTypeDTO.id(),
                userTypeDTO.name(),
                userTypeDTO.permissions());

    }

    @Override
    public Optional<UserType> findByName(String name) {
        final UserTypeDTO userTypeDTO = this.dataStorageSource.findByName(name);

        if (userTypeDTO == null) {
            return Optional.empty();
        }

        var userType = UserType.create(
                userTypeDTO.id(),
                userTypeDTO.name(),
                userTypeDTO.permissions());

        return Optional.of(userType);
    }

}
