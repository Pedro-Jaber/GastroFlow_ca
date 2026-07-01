package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserType;

public interface IUserTypeGateway {

    UserType saveNewUserType(UserType newUserType);

    PageOutputDTO<UserType> findAll(PageInputDTO pageInput);

    Optional<UserType> findById(UUID id);

    Optional<UserType> findByName(String name);

    UserType updateUserType(UserType existingUserType);

}
