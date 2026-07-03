package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;

public interface IUserDataSource {

    UserDTO saveNewUser(UserDTO userDTO);

    PageOutputDTO<UserDTO> findAll(PageInputDTO pageInput);

    Optional<UserDTO> findById(UUID id);

    Optional<UserDTO> findByLogin(String login);

    UserDTO updateUser(UserDTO userDTO);

    void deleteById(UUID id);

}
