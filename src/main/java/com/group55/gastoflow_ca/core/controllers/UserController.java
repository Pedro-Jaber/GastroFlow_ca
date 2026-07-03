package com.group55.gastoflow_ca.core.controllers;

import java.util.List;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.gateways.UserGateway;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.presenters.UserPresenter;
import com.group55.gastoflow_ca.core.usecases.user.CreateUserUseCase;
import com.group55.gastoflow_ca.core.usecases.user.GetAllUserUseCase;
import com.group55.gastoflow_ca.core.usecases.user.GetUserByIdUseCase;

public class UserController {

    private final IUserDataSource userDataSource;
    private final IUserTypeDataSource userTypeDataSource;

    private final UserGateway userGateway;

    private UserController(IUserDataSource userDataSource, IUserTypeDataSource userTypeDataSource) {
        this.userDataSource = userDataSource;
        this.userTypeDataSource = userTypeDataSource;

        this.userGateway = UserGateway.create(this.userDataSource);
    }

    public static UserController create(IUserDataSource userDataSource, IUserTypeDataSource userTypeDataSource) {
        return new UserController(userDataSource, userTypeDataSource);
    }

    public UserOutputDTO createUser(CreateUserInputDataDTO newUserDTO) {
        // UserGateway userGateway = UserGateway.create(this.userDataSource);
        UserTypeGateway userTypeGateway = UserTypeGateway.create(this.userTypeDataSource);
        CreateUserUseCase useCase = CreateUserUseCase.create(this.userGateway, userTypeGateway);

        var user = useCase.run(newUserDTO);
        var userOutDTO = UserPresenter.toOutputDTO(user);
        return userOutDTO;
    }

    public PageOutputDTO<UserOutputDTO> getAllUser(PageInputDTO pageInput) {

        GetAllUserUseCase useCase = GetAllUserUseCase.create(userGateway);

        PageOutputDTO<User> page = useCase.run(pageInput);

        List<UserOutputDTO> content = page.content().stream()
                .map(UserPresenter::toOutputDTO)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    public UserOutputDTO getUserById(UUID id) {
        GetUserByIdUseCase useCase = GetUserByIdUseCase.create(userGateway);

        User user = useCase.run(id);

        var userOutputDTO = UserPresenter.toOutputDTO(user);
        return userOutputDTO;
    }

}
