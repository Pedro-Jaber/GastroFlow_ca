package com.group55.gastoflow_ca.core.gateways;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class UserGateway implements IUserGateway {

    private final IUserDataSource dataStorageSource;

    private UserGateway(IUserDataSource dataSource) {
        this.dataStorageSource = dataSource;
    }

    public static UserGateway create(IUserDataSource dataSource) {
        return new UserGateway(dataSource);
    }

    @Override
    public User saveNewUser(User user) {
        final UserDTO newUserDTO = toDTO(user);

        final UserDTO createdUser = this.dataStorageSource.saveNewUser(newUserDTO);

        return toEntity(createdUser);
    }

    @Override
    public PageOutputDTO<User> findAll(PageInputDTO pageInput) {

        PageOutputDTO<UserDTO> page = this.dataStorageSource.findAll(pageInput);

        List<User> content = page.content().stream()
                .map(dto -> toEntity(dto))
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    @Override
    public Optional<User> findById(UUID id) {
        return this.dataStorageSource.findById(id)
                .map(dto -> toEntity(dto));

    }

    @Override
    public Optional<User> findByLogin(String login) {
        return this.dataStorageSource.findByLogin(login)
                .map(dto -> toEntity(dto));
    }

    public User toEntity(UserDTO userDTO) {
        return User.create(
                userDTO.id(),
                userDTO.name(),
                userDTO.emailAddress(),
                userDTO.login(),
                userDTO.password(),
                UserType.create(
                        userDTO.userTypeDTO().id(),
                        userDTO.userTypeDTO().name(),
                        userDTO.userTypeDTO().permissions()),
                userDTO.createdAt(),
                userDTO.updatedAt());
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmailAddress(),
                user.getLogin(),
                user.getPassword(),
                new UserTypeDTO(
                        user.getUserType().getId(),
                        user.getUserType().getName(),
                        user.getUserType().getPermissions()),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
