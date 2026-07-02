package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.Optional;

import com.group55.gastoflow_ca.core.dtos.user.UserDTO;

public interface IUserDataSource {

    UserDTO saveNewUser(UserDTO userDTO);

    Optional<UserDTO> findByLogin(String login);
}
