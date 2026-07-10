package com.group55.gastoflow_ca.core.usecases.auth;

import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.exceptions.InvalidCredentialsException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class LoginUseCase {

    private final IUserGateway userGateway;

    private final IPasswordHasher passwordHasher;

    private LoginUseCase(
            IUserGateway userGateway,
            IPasswordHasher passwordHasher) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
    }

    public static LoginUseCase create(
            IUserGateway userGateway,
            IPasswordHasher passwordHasher) {
        return new LoginUseCase(userGateway, passwordHasher);
    }

    public UserToken run(LoginInputDataDTO loginResquest) {

        User existingUser = userGateway.findByLogin(loginResquest.login()).orElseThrow(
                () -> new InvalidCredentialsException("Invalid Credentials"));

        boolean validPassword = passwordHasher.matches(loginResquest.password(), existingUser.getPassword());

        if (!validPassword) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        UserToken token = UserToken.create(existingUser);

        return token;
    }

}
