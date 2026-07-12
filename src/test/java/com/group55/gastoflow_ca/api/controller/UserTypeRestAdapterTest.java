package com.group55.gastoflow_ca.api.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.group55.gastoflow_ca.api.dto.request.CreateUserTypeRequest;
import com.group55.gastoflow_ca.api.dto.request.UpdateUserTypeResquest;
import com.group55.gastoflow_ca.core.controllers.UserTypeController;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UpdateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;

@ExtendWith(MockitoExtension.class)
class UserTypeRestAdapterTest {

    @Mock
    private UserTypeController userTypeController;

    private UserTypeRestAdapter adapter;

    private UserToken userToken;

    @BeforeEach
    void setup() {
        adapter = new UserTypeRestAdapter(userTypeController);

        UserType userType = UserType.create(UUID.randomUUID(), "Admin", Set.of());
        userToken = new UserToken(UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", userType);
    }

    @Test
    void shouldCreateUserTypeAndReturn201() {
        var request = new CreateUserTypeRequest("Cliente", Set.of(Permission.READ_RESTAURANT));
        var output = new UserTypeOutputDTO(UUID.randomUUID(), "Cliente", Set.of(Permission.READ_RESTAURANT));

        when(userTypeController.createUserType(eq(userToken), any(CreateUserTypeInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<UserTypeOutputDTO> response = adapter.create(userToken, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
        verify(userTypeController).createUserType(userToken,
                new CreateUserTypeInputDataDTO("Cliente", Set.of(Permission.READ_RESTAURANT)));
    }

    @Test
    void shouldReturnPageOfUserTypes() {
        var output = new UserTypeOutputDTO(UUID.randomUUID(), "Cliente", Set.of());
        var page = new PageOutputDTO<>(List.of(output), 0, 10, 1L, 1);

        when(userTypeController.getAllUserType(eq(userToken), any(PageInputDTO.class))).thenReturn(page);

        ResponseEntity<PageOutputDTO<UserTypeOutputDTO>> response = adapter.getAll(userToken, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
    }

    @Test
    void shouldReturnUserTypeById() {
        UUID id = UUID.randomUUID();
        var output = new UserTypeOutputDTO(id, "Cliente", Set.of());

        when(userTypeController.getUserTypeById(userToken, id)).thenReturn(output);

        ResponseEntity<UserTypeOutputDTO> response = adapter.getById(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void shouldUpdateUserType() {
        UUID id = UUID.randomUUID();
        var request = new UpdateUserTypeResquest("Gerente", Set.of(Permission.EDIT_RESTAURANT));
        var output = new UserTypeOutputDTO(id, "Gerente", Set.of(Permission.EDIT_RESTAURANT));

        when(userTypeController.updateUserType(eq(userToken), eq(id), any(UpdateUserTypeInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<UserTypeOutputDTO> response = adapter.update(userToken, id, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
        verify(userTypeController).updateUserType(userToken, id,
                new UpdateUserTypeInputDataDTO("Gerente", Set.of(Permission.EDIT_RESTAURANT)));
    }

    @Test
    void shouldDeleteUserTypeAndReturn204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = adapter.delete(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userTypeController, times(1)).deleteUserType(userToken, id);
    }
}
