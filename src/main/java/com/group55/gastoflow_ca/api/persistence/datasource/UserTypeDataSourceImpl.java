package com.group55.gastoflow_ca.api.persistence.datasource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.api.persistence.entity.UserTypeJpaEntity;
import com.group55.gastoflow_ca.api.persistence.repository.UserTypeJpaRepository;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
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
    public PageOutputDTO<UserTypeDTO> findAll(PageInputDTO pageInput) {

        Pageable pageable = PageRequest.of(pageInput.page(), pageInput.size());
        Page<UserTypeJpaEntity> page = userTypeJpaRepository.findAll(pageable);

        List<UserTypeDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public Optional<UserTypeDTO> findById(UUID id) {
        return userTypeJpaRepository.findById(id)
                .map(this::toDTO);
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