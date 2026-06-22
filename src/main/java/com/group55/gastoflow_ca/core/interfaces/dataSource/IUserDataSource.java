package com.group55.gastoflow_ca.core.interfaces.dataSource;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;

public interface IUserDataSource {

    UserDTO saveUser(CreateUserInputDataDTO createUserInputDataDTO);

    UserDTO findByLogin(String login);
}
