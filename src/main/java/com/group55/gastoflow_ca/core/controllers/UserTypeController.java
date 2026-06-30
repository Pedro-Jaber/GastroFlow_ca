package com.group55.gastoflow_ca.core.controllers;

import java.util.List;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.presenters.UserTypePresenter;
import com.group55.gastoflow_ca.core.usecases.userType.CreateUserTypeUseCase;
import com.group55.gastoflow_ca.core.usecases.userType.GetAllUserTypeUseCase;

public class UserTypeController {

    private final IUserTypeDataSource userTypeDataSource;

    private UserTypeController(IUserTypeDataSource userTypeDataSource) {
        this.userTypeDataSource = userTypeDataSource;
    }

    public static UserTypeController create(IUserTypeDataSource userTypeDataSource) {
        return new UserTypeController(userTypeDataSource);
    }

    public UserTypeOutputDTO createUserType(CreateUserTypeInputDataDTO input) {
        UserTypeGateway userTypeGateway = UserTypeGateway.create(userTypeDataSource);
        CreateUserTypeUseCase useCase = CreateUserTypeUseCase.create(userTypeGateway);

        var userType = useCase.run(input);
        var userTypeOutputDTO = UserTypePresenter.toOutputDTO(userType);

        return userTypeOutputDTO;
    }

    public PageOutputDTO<UserTypeOutputDTO> GetAllUserType(PageInputDTO pageInput) {
        UserTypeGateway userTypeGateway = UserTypeGateway.create(userTypeDataSource);
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

}
