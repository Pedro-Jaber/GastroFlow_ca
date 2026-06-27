package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;

public interface IUserTypeDataSource {

    UserTypeDTO saveNewUserType(UserTypeDTO newUserTypeDTO);

    UserTypeDTO findById(UUID id);

    Optional<UserTypeDTO> findByName(String name);

}
