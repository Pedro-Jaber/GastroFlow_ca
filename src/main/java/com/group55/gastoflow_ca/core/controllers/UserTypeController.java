package com.group55.gastoflow_ca.core.controllers;

import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.presenters.UserTypePresenter;
import com.group55.gastoflow_ca.core.usecases.userType.CreateUserTypeUseCase;

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

}
