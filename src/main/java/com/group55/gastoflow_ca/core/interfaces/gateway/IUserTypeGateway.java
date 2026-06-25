package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.entities.UserType;

public interface IUserTypeGateway {

    UserType findById(UUID id);

    Optional<UserType> findByName(String name);

    UserType saveNewUserType(UserType newUserType);
}
