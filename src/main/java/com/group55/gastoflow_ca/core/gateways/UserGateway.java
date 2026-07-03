package com.group55.gastoflow_ca.core.gateways;

import java.util.List;
import java.util.Optional;

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
        final UserDTO newUserDTO = new UserDTO(
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

        final UserDTO createdUser = this.dataStorageSource.saveNewUser(newUserDTO);

        return User.create(
                createdUser.id(),
                createdUser.name(),
                createdUser.emailAddress(),
                createdUser.login(),
                createdUser.password(),
                UserType.create(
                        createdUser.userTypeDTO().id(),
                        createdUser.userTypeDTO().name(),
                        createdUser.userTypeDTO().permissions()),
                createdUser.createdAt(),
                createdUser.updatedAt());
    }

    @Override
    public PageOutputDTO<User> findAll(PageInputDTO pageInput) {

        PageOutputDTO<UserDTO> page = this.dataStorageSource.findAll(pageInput);

        List<User> content = page.content().stream()
                .map(dto -> User.create(
                        dto.id(),
                        dto.name(),
                        dto.emailAddress(),
                        dto.login(),
                        dto.password(),
                        UserType.create(
                                dto.userTypeDTO().name(),
                                dto.userTypeDTO().permissions()),
                        dto.createdAt(),
                        dto.updatedAt()))
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return this.dataStorageSource.findByLogin(login)
                .map(dto -> User.create(
                        dto.id(),
                        dto.name(),
                        dto.emailAddress(),
                        dto.login(),
                        dto.password(),
                        UserType.create(
                                dto.userTypeDTO().id(),
                                dto.userTypeDTO().name(),
                                dto.userTypeDTO().permissions()),
                        dto.createdAt(),
                        dto.updatedAt()));
    }

}
