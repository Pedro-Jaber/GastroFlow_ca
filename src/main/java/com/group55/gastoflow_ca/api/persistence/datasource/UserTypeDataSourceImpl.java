package com.group55.gastoflow_ca.api.persistence.datasource;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.api.persistence.entity.UserTypeJpaEntity;
import com.group55.gastoflow_ca.api.persistence.repository.UserTypeJpaRepository;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;

@Component
public class UserTypeDataSourceImpl implements IUserTypeDataSource {

    private final UserTypeJpaRepository userTypeJpaRepository;

    public UserTypeDataSourceImpl(UserTypeJpaRepository userTypeJpaRepository) {
        this.userTypeJpaRepository = userTypeJpaRepository;
    }

    @Override
    public UserTypeDTO saveNewUserType(UserTypeDTO newUserTypeDTO) {
        UserTypeJpaEntity entity = new UserTypeJpaEntity();
        entity.setId(newUserTypeDTO.id());
        entity.setName(newUserTypeDTO.name());
        entity.setPermissions(newUserTypeDTO.permissions());

        UserTypeJpaEntity saved = userTypeJpaRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public UserTypeDTO findById(UUID id) {
        return userTypeJpaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new UserTypeNotFoundException("UserType with id " + id + " not found."));
    }

    @Override
    public Optional<UserTypeDTO> findByName(String name) {
        return userTypeJpaRepository.findByName(name)
                .map(this::toDTO);
    }

    private UserTypeDTO toDTO(UserTypeJpaEntity entity) {
        return new UserTypeDTO(
                entity.getId(),
                entity.getName(),
                entity.getPermissions());
    }
}