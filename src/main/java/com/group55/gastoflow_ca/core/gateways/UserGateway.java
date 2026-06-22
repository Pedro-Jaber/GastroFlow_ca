package com.group55.gastoflow_ca.core.gateways;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class UserGateway implements IUserGateway {

    private final IUserDataSource dataStorageSource;

    private UserGateway(IUserDataSource dataSource) {
        this.dataStorageSource = dataSource;
    }

    public static UserGateway create(IUserDataSource dataSource) {
        return new UserGateway(dataSource);
    }

    @Override
    public User saveNewUser(User user) {
        final CreateUserInputDataDTO newUserDTO = new CreateUserInputDataDTO(
                user.getName(),
                user.getEmailAddress(),
                user.getLogin(),
                user.getPassword(),
                user.getUserType().getId());

        final UserDTO createdUser = this.dataStorageSource.saveUser(newUserDTO);
        return User.create(
                createdUser.id(),
                createdUser.name(),
                createdUser.emailAddress(),
                createdUser.login(),
                createdUser.password(),
                UserType.create(
                        createdUser.userTypeDTO().id(),
                        createdUser.userTypeDTO().name(),
                        createdUser.userTypeDTO().permissions()));
    }

    @Override
    public User findByLogin(String login) {
        final UserDTO userDTO = this.dataStorageSource.findByLogin(login);

        if (userDTO == null) {
            throw new UserNotFoundException("User with login " + login + " not found.");
        }

        return User.create(
                userDTO.id(),
                userDTO.name(),
                userDTO.emailAddress(),
                userDTO.login(),
                userDTO.password(),
                UserType.create(
                        userDTO.userTypeDTO().id(),
                        userDTO.userTypeDTO().name(),
                        userDTO.userTypeDTO().permissions()));
    }

}
