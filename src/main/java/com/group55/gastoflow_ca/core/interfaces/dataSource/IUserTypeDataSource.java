package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;

public interface IUserTypeDataSource {

    UserTypeDTO saveNewUserType(UserTypeDTO newUserTypeDTO);

    PageOutputDTO<UserTypeDTO> findAll(PageInputDTO pageInput);

    Optional<UserTypeDTO> findById(UUID id);

    Optional<UserTypeDTO> findByName(String name);

    UserTypeDTO updateUserType(UserTypeDTO userTypeDTOToUpdate);

}
