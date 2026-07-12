package com.group55.gastoflow_ca.core.controllers;

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
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UpdateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;

@ExtendWith(MockitoExtension.class)
class UserTypeControllerTest {

    @Mock
    private IUserTypeDataSource userTypeDataSource;

    private UserTypeController controller;

    private UserToken adminToken;

    @BeforeEach
    void setup() {
        controller = UserTypeController.create(userTypeDataSource);

        UserType adminUserType = UserType.create(UUID.randomUUID(), "Admin", Set.of(Permission.values()));
        adminToken = new UserToken(UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", adminUserType);
    }

    @Test
    void shouldCreateUserTypeSuccessfully() {
        when(userTypeDataSource.findByName("Cliente")).thenReturn(Optional.empty());
        when(userTypeDataSource.saveNewUserType(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new CreateUserTypeInputDataDTO("Cliente", Set.of(Permission.READ_RESTAURANT));
        UserTypeOutputDTO output = controller.createUserType(adminToken, input);

        assertThat(output.name()).isEqualTo("Cliente");
        assertThat(output.permissions()).containsExactly(Permission.READ_RESTAURANT);
        verify(userTypeDataSource, times(1)).saveNewUserType(any());
    }

    @Test
    void shouldThrowForbiddenWhenCreatingUserTypeWithoutPermission() {
        UserType noPermissionType = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken tokenWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", noPermissionType);

        var input = new CreateUserTypeInputDataDTO("Gerente", Set.of());

        assertThatThrownBy(() -> controller.createUserType(tokenWithoutPermission, input))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void shouldReturnPageOfUserTypes() {
        var userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of());

        var pageInput = new PageInputDTO(0, 10);
        when(userTypeDataSource.findAll(pageInput))
                .thenReturn(new PageOutputDTO<>(List.of(userTypeDTO), 0, 10, 1L, 1));

        PageOutputDTO<UserTypeOutputDTO> result = controller.getAllUserType(adminToken, pageInput);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Cliente");
    }

    @Test
    void shouldReturnUserTypeById() {
        UUID id = UUID.randomUUID();
        var userTypeDTO = new UserTypeDTO(id, "Cliente", Set.of());

        when(userTypeDataSource.findById(id)).thenReturn(Optional.of(userTypeDTO));

        UserTypeOutputDTO result = controller.getUserTypeById(adminToken, id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Cliente");
    }

    @Test
    void shouldUpdateUserTypeSuccessfully() {
        UUID id = UUID.randomUUID();
        var existingDTO = new UserTypeDTO(id, "Cliente", Set.of());

        when(userTypeDataSource.findById(id)).thenReturn(Optional.of(existingDTO));
        when(userTypeDataSource.updateUserType(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new UpdateUserTypeInputDataDTO("Gerente", Set.of(Permission.EDIT_RESTAURANT));
        UserTypeOutputDTO result = controller.updateUserType(adminToken, id, input);

        assertThat(result.name()).isEqualTo("Gerente");
        assertThat(result.permissions()).containsExactly(Permission.EDIT_RESTAURANT);
        verify(userTypeDataSource, times(1)).updateUserType(any());
    }

    @Test
    void shouldDeleteUserType() {
        UUID id = UUID.randomUUID();

        controller.deleteUserType(adminToken, id);

        verify(userTypeDataSource, times(1)).deleteById(id);
    }
}
