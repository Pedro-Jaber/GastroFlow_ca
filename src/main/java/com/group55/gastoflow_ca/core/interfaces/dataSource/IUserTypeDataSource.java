package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;

public interface IUserTypeDataSource {

    UserTypeDTO findById(UUID id);
}
