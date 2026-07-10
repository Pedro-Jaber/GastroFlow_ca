package com.group55.gastoflow_ca.core.usecases.user;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class CreateUserUseCase {

    private final IUserGateway userGateway;
    private final IUserTypeGateway userTypeGateway;

    private final IPasswordHasher passwordHasher;

    private CreateUserUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway,
            IPasswordHasher passwordHasher) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
        this.passwordHasher = passwordHasher;
    }

    public static CreateUserUseCase create(IUserGateway userGateway, IUserTypeGateway userTypeGateway,
            IPasswordHasher passwordHasher) {
        return new CreateUserUseCase(userGateway, userTypeGateway, passwordHasher);
    }

    public User run(CreateUserInputDataDTO createUserInputDataDTO) {

        this.userGateway.findByLogin(createUserInputDataDTO.login())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException(
                            "User with login " + createUserInputDataDTO.login() + " already exists.");
                });

        final UserType userType = this.userTypeGateway.findById(createUserInputDataDTO.userTypeId())
                .orElseThrow(() -> new UserTypeNotFoundException(
                        "UserType with id " + createUserInputDataDTO.userTypeId() + " not found."));

        final String encodedPassword = this.passwordHasher.encode(createUserInputDataDTO.password());

        final User newUser = User.create(
                createUserInputDataDTO.name(),
                createUserInputDataDTO.emailAddress(),
                createUserInputDataDTO.login(),
                encodedPassword,
                userType);

        final User user = this.userGateway.saveNewUser(newUser);

        return user;
    }

}
