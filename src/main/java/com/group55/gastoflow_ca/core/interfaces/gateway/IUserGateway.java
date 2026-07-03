package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.User;

public interface IUserGateway {

    User saveNewUser(User user);

    PageOutputDTO<User> findAll(PageInputDTO pageInput);

    Optional<User> findById(UUID id);

    Optional<User> findByLogin(String login);

    User updateUser(User existingUser);

    void deleteById(UUID id);

}
