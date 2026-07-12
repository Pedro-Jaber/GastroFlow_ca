package com.group55.gastoflow_ca.core.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserDataSource userDataSource;

    @Mock
    private IUserTypeDataSource userTypeDataSource;

    @Mock
    private IPasswordHasher passwordHasher;

    private UserController controller;

    private UserToken adminToken;

    @BeforeEach
    void setup() {
        controller = UserController.create(userDataSource, userTypeDataSource, passwordHasher);

        UserType adminUserType = UserType.create(UUID.randomUUID(), "Admin", Set.of(Permission.values()));
        adminToken = new UserToken(UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", adminUserType);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UUID userTypeId = UUID.randomUUID();
        UserTypeDTO userTypeDTO = new UserTypeDTO(userTypeId, "Cliente", Set.of());

        when(userDataSource.findByLogin("jdoe")).thenReturn(Optional.empty());
        when(userTypeDataSource.findById(userTypeId)).thenReturn(Optional.of(userTypeDTO));
        when(passwordHasher.encode("password123")).thenReturn("encoded-password123");
        when(userDataSource.saveNewUser(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new CreateUserInputDataDTO("John Doe", "jdoe@ex.com", "jdoe", "password123", userTypeId);
        UserOutputDTO output = controller.createUser(adminToken, input);

        assertThat(output.name()).isEqualTo("John Doe");
        assertThat(output.login()).isEqualTo("jdoe");
        assertThat(output.userTypeDTO().name()).isEqualTo("Cliente");
        verify(userDataSource, times(1)).saveNewUser(any());
    }

    @Test
    void shouldThrowForbiddenWhenCreatingUserWithoutPermission() {
        UserType noPermissionType = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken tokenWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", noPermissionType);

        var input = new CreateUserInputDataDTO("John Doe", "jdoe@ex.com", "jdoe", "password123", UUID.randomUUID());

        assertThatThrownBy(() -> controller.createUser(tokenWithoutPermission, input))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void shouldReturnPageOfUsers() {
        UserTypeDTO userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of());
        UserDTO userDTO = new UserDTO(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "encoded",
                userTypeDTO, LocalDateTime.now(), LocalDateTime.now());

        var pageInput = new PageInputDTO(0, 10);
        when(userDataSource.findAll(pageInput)).thenReturn(new PageOutputDTO<>(List.of(userDTO), 0, 10, 1L, 1));

        PageOutputDTO<UserOutputDTO> result = controller.getAllUser(adminToken, pageInput);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).login()).isEqualTo("jdoe");
    }

    @Test
    void shouldReturnUserById() {
        UUID id = UUID.randomUUID();
        UserTypeDTO userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of());
        UserDTO userDTO = new UserDTO(id, "John Doe", "jdoe@ex.com", "jdoe", "encoded",
                userTypeDTO, LocalDateTime.now(), LocalDateTime.now());

        when(userDataSource.findById(id)).thenReturn(Optional.of(userDTO));

        UserOutputDTO result = controller.getUserById(adminToken, id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.login()).isEqualTo("jdoe");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UUID id = UUID.randomUUID();
        UserTypeDTO userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of());
        UserDTO existingUserDTO = new UserDTO(id, "John Doe", "jdoe@ex.com", "jdoe", "encoded",
                userTypeDTO, LocalDateTime.now(), LocalDateTime.now());

        when(userDataSource.findById(id)).thenReturn(Optional.of(existingUserDTO));
        when(userDataSource.updateUser(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new UpdateUserInputDataDTO("John Updated", null, null, null, null);
        UserOutputDTO result = controller.updateUser(adminToken, id, input);

        assertThat(result.name()).isEqualTo("John Updated");
        verify(userDataSource, times(1)).updateUser(any());
    }

    @Test
    void shouldDeleteUser() {
        UUID id = UUID.randomUUID();

        controller.deleteUser(adminToken, id);

        verify(userDataSource, times(1)).deleteById(id);
    }
}
