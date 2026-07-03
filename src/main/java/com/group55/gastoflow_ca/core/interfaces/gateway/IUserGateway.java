package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.User;

public interface IUserGateway {

    User saveNewUser(User user);

    Optional<User> findByLogin(String login);

    PageOutputDTO<User> findAll(PageInputDTO pageInput);

}
