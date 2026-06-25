package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;

public interface IUserTypeDataSource {

    UserTypeDTO saveNewUserType(CreateUserTypeInputDataDTO newUserTypeDTO);

    UserTypeDTO findById(UUID id);

    UserTypeDTO findByName(String name);

}
