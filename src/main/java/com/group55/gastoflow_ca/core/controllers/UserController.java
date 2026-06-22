package com.group55.gastoflow_ca.core.controllers;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;
import com.group55.gastoflow_ca.core.gateways.UserGateway;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.presenters.UserPresenter;
import com.group55.gastoflow_ca.core.usecases.user.CreateUserUseCase;

public class UserController {

    private final IUserDataSource userDataStorageSource;
    private final IUserTypeDataSource userTypeDataStorageSource;

    private UserController(IUserDataSource dataSource, IUserTypeDataSource userTypeDataStorageSource) {
        this.userDataStorageSource = dataSource;
        this.userTypeDataStorageSource = userTypeDataStorageSource;
    }

    public static UserController create(IUserDataSource dataSource, IUserTypeDataSource userTypeDataStorageSource) {
        return new UserController(dataSource, userTypeDataStorageSource);
    }

    public UserOutputDTO createUser(CreateUserInputDataDTO newUserDTO) {
        UserGateway userGateway = UserGateway.create(this.userDataStorageSource);
        UserTypeGateway userTypeGateway = UserTypeGateway.create(this.userTypeDataStorageSource);
        CreateUserUseCase useCase = CreateUserUseCase.create(userGateway, userTypeGateway);

        var user = useCase.run(newUserDTO);
        var userOutDTO = UserPresenter.toOutputDTO(user);
        return userOutDTO;
    }
}
