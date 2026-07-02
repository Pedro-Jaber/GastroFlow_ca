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

    private final IUserDataSource userDataSource;
    private final IUserTypeDataSource userTypeDataSource;

    private UserController(IUserDataSource userDataSource, IUserTypeDataSource userTypeDataSource) {
        this.userDataSource = userDataSource;
        this.userTypeDataSource = userTypeDataSource;
    }

    public static UserController create(IUserDataSource userDataSource, IUserTypeDataSource userTypeDataSource) {
        return new UserController(userDataSource, userTypeDataSource);
    }

    public UserOutputDTO createUser(CreateUserInputDataDTO newUserDTO) {
        UserGateway userGateway = UserGateway.create(this.userDataSource);
        UserTypeGateway userTypeGateway = UserTypeGateway.create(this.userTypeDataSource);
        CreateUserUseCase useCase = CreateUserUseCase.create(userGateway, userTypeGateway);

        var user = useCase.run(newUserDTO);
        var userOutDTO = UserPresenter.toOutputDTO(user);
        return userOutDTO;
    }

}
