package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.UUID;

import com.group55.gastoflow_ca.core.entities.UserType;

public interface IUserTypeGateway {

    UserType findById(UUID id);
}
