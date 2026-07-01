package com.group55.gastoflow_ca.core.usecases.user;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class CreateUserUseCase {

    private final IUserGateway userGateway;
    private final IUserTypeGateway userTypeGateway;

    private CreateUserUseCase(IUserGateway userGateway, IUserTypeGateway userTypeGateway) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
    }

    public static CreateUserUseCase create(IUserGateway userGateway, IUserTypeGateway userTypeGateway) {
        return new CreateUserUseCase(userGateway, userTypeGateway);
    }

    public User run(CreateUserInputDataDTO createUserInputDataDTO) {

        final User existentUser = this.userGateway.findByLogin(createUserInputDataDTO.login());

        if (existentUser != null) {
            throw new UserAlreadyExistsException(
                    "User with login " + createUserInputDataDTO.login() + " already exists.");
        }

        final UserType userType = this.userTypeGateway.findById(createUserInputDataDTO.userTypeId())
                .orElseThrow(() -> new UserTypeNotFoundException(
                        "UserType with id " + createUserInputDataDTO.userTypeId() + " not found."));

        final User newUser = User.create(createUserInputDataDTO.name(),
                createUserInputDataDTO.emailAddress(),
                createUserInputDataDTO.login(),
                createUserInputDataDTO.password(),
                userType);

        User user = this.userGateway.saveNewUser(newUser);

        return user;
    }

}
