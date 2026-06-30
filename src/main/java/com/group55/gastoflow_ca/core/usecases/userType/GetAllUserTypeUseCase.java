package com.group55.gastoflow_ca.core.usecases.userType;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

public class GetAllUserTypeUseCase {

    private final IUserTypeGateway userTypeGateway;

    private GetAllUserTypeUseCase(IUserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    public static GetAllUserTypeUseCase create(IUserTypeGateway userTypeGateway) {
        return new GetAllUserTypeUseCase(userTypeGateway);
    }

    public PageOutputDTO<UserType> run(PageInputDTO pageInput) {
        return userTypeGateway.findAll(pageInput);
    }

}
