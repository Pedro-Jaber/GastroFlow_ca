package com.group55.gastoflow_ca.core.gateways;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
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
        final UserTypeDTO newUserTypeDTO = new UserTypeDTO(
                newUserType.getId(),
                newUserType.getName(),
                newUserType.getPermissions());

        final UserTypeDTO createdUserType = this.dataStorageSource.saveNewUserType(newUserTypeDTO);

        return UserType.create(
                createdUserType.id(),
                createdUserType.name(),
                createdUserType.permissions());
    }

    @Override
    public PageOutputDTO<UserType> findAll(PageInputDTO pageInput) {

        PageOutputDTO<UserTypeDTO> page = this.dataStorageSource.findAll(pageInput);

        List<UserType> content = page.content().stream()
                .map(dto -> UserType.create(dto.id(), dto.name(), dto.permissions()))
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
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
        return this.dataStorageSource.findByName(name)
                .map(dto -> UserType.create(dto.id(), dto.name(), dto.permissions()));
    }

}
