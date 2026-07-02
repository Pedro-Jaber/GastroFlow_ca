package com.group55.gastoflow_ca.api.persistence.datasource;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.api.persistence.entity.UserJpaEntity;
import com.group55.gastoflow_ca.api.persistence.entity.UserTypeJpaEntity;
import com.group55.gastoflow_ca.api.persistence.repository.UserJpaRepository;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;

@Component
public class UserDataSourceImpl implements IUserDataSource {

    private final UserJpaRepository userJpaRepository;

    public UserDataSourceImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDTO saveNewUser(UserDTO userDTO) {
        UserJpaEntity entity = toEntity(userDTO);

        UserJpaEntity saved = userJpaRepository.save(entity);

        return toDTO(saved);
    }

    @Override
    public Optional<UserDTO> findByLogin(String login) {
        return userJpaRepository.findByLogin(login).map(this::toDTO);
    }

    private UserJpaEntity toEntity(UserDTO dto) {
        return new UserJpaEntity(
                dto.id(),
                dto.name(),
                dto.emailAddress(),
                dto.login(),
                dto.password(),
                new UserTypeJpaEntity(
                        dto.userTypeDTO().id(),
                        dto.userTypeDTO().name(),
                        dto.userTypeDTO().permissions()),
                dto.createdAt(),
                dto.updatedAt());
    }

    private UserDTO toDTO(UserJpaEntity entity) {
        return new UserDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmailAddress(),
                entity.getLogin(),
                entity.getPassword(),
                new UserTypeDTO(
                        entity.getUserType().getId(),
                        entity.getUserType().getName(),
                        entity.getUserType().getPermissions()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
