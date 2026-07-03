package com.group55.gastoflow_ca.core.usecases.user;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class GetAllUserUseCase {

    private final IUserGateway userGateway;

    private GetAllUserUseCase(IUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public static GetAllUserUseCase create(IUserGateway userGateway) {
        return new GetAllUserUseCase(userGateway);
    }

    public PageOutputDTO<User> run(PageInputDTO pageInput) {
        return userGateway.findAll(pageInput);
    }
}
