package com.group55.gastoflow_ca.core.controllers;

import java.util.List;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UpdateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.presenters.UserTypePresenter;
import com.group55.gastoflow_ca.core.usecases.userType.CreateUserTypeUseCase;
import com.group55.gastoflow_ca.core.usecases.userType.DeleteUserTypeUseCase;
import com.group55.gastoflow_ca.core.usecases.userType.GetAllUserTypeUseCase;
import com.group55.gastoflow_ca.core.usecases.userType.GetUserTypeByIdUseCase;
import com.group55.gastoflow_ca.core.usecases.userType.UpdateUserTypeUseCase;

public class UserTypeController {

    private final IUserTypeDataSource userTypeDataSource;

    private final UserTypeGateway userTypeGateway;

    private UserTypeController(IUserTypeDataSource userTypeDataSource) {
        this.userTypeDataSource = userTypeDataSource;

        this.userTypeGateway = UserTypeGateway.create(this.userTypeDataSource);
    }

    public static UserTypeController create(IUserTypeDataSource userTypeDataSource) {
        return new UserTypeController(userTypeDataSource);
    }

    public UserTypeOutputDTO createUserType(CreateUserTypeInputDataDTO input) {
        CreateUserTypeUseCase useCase = CreateUserTypeUseCase.create(userTypeGateway);

        var userType = useCase.run(input);
        var userTypeOutputDTO = UserTypePresenter.toOutputDTO(userType);

        return userTypeOutputDTO;
    }

    public PageOutputDTO<UserTypeOutputDTO> GetAllUserType(PageInputDTO pageInput) {
        GetAllUserTypeUseCase useCase = GetAllUserTypeUseCase.create(userTypeGateway);

        PageOutputDTO<UserType> page = useCase.run(pageInput);

        List<UserTypeOutputDTO> content = page.content().stream()
                .map(UserTypePresenter::toOutputDTO)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    public UserTypeOutputDTO GetUserTypeById(UUID id) {
        GetUserTypeByIdUseCase useCase = GetUserTypeByIdUseCase.create(userTypeGateway);

        var userType = useCase.run(id);
        var userTypeOutputDTO = UserTypePresenter.toOutputDTO(userType);

        return userTypeOutputDTO;
    }

    public UserTypeOutputDTO updateUserType(UUID id, UpdateUserTypeInputDataDTO input) {
        UpdateUserTypeUseCase useCase = UpdateUserTypeUseCase.create(userTypeGateway);

        var userType = useCase.run(id, input);
        var userTypeOutputDTO = UserTypePresenter.toOutputDTO(userType);

        return userTypeOutputDTO;
    }

    public void deleteUserType(UUID id) {
        DeleteUserTypeUseCase useCase = DeleteUserTypeUseCase.create(userTypeGateway);

        useCase.run(id);
    }

}
