package com.group55.gastoflow_ca.core.interfaces.gateway;

import com.group55.gastoflow_ca.core.entities.User;

public interface IUserGateway {

    User saveNewUser(User user);

    User findByLogin(String login);

}
