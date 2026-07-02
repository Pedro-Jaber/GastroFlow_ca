package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;

import com.group55.gastoflow_ca.core.entities.User;

public interface IUserGateway {

    User saveNewUser(User user);

    Optional<User> findByLogin(String login);

}
