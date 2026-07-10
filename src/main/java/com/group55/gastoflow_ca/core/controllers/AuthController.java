package com.group55.gastoflow_ca.core.controllers;

import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.gateways.UserGateway;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.presenters.UserPresenter;
import com.group55.gastoflow_ca.core.usecases.auth.LoginUseCase;

public class AuthController {

    private final IUserDataSource userDataSource;
    private final IPasswordHasher passwordHasher;

    private final IUserGateway userGateway;

    private AuthController(IUserDataSource userDataSource, IPasswordHasher passwordHasher) {
        this.userDataSource = userDataSource;
        this.passwordHasher = passwordHasher;

        this.userGateway = UserGateway.create(this.userDataSource);
    }

    public static AuthController create(IUserDataSource userDataSource, IPasswordHasher passwordHasher) {
        return new AuthController(userDataSource, passwordHasher);
    }

    public UserOutputDTO login(LoginInputDataDTO loginResquest) {

        LoginUseCase useCase = LoginUseCase.create(userGateway, passwordHasher);

        User user = useCase.run(loginResquest);
        UserOutputDTO userDTO = UserPresenter.toOutputDTO(user);

        return userDTO;

    }

}
