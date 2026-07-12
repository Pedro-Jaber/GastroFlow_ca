package com.group55.gastoflow_ca.api.controller;

import java.time.LocalDateTime;
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

import com.group55.gastoflow_ca.api.dto.request.user.CreateUserRequest;
import com.group55.gastoflow_ca.api.dto.request.user.UpdateUserRequest;
import com.group55.gastoflow_ca.core.controllers.UserController;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;

@ExtendWith(MockitoExtension.class)
class UserRestAdapterTest {

    @Mock
    private UserController userController;

    private UserRestAdapter adapter;

    private UserToken userToken;

    @BeforeEach
    void setup() {
        adapter = new UserRestAdapter(userController);

        UserType userType = UserType.create(UUID.randomUUID(), "Admin", Set.of());
        userToken = new UserToken(UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", userType);
    }

    @Test
    void shouldCreateUserAndReturn201() {
        UUID userTypeId = UUID.randomUUID();
        var request = new CreateUserRequest("John Doe", "jdoe@ex.com", "jdoe", "password123", userTypeId);
        var output = new UserOutputDTO(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe",
                new UserTypeDTO(userTypeId, "Cliente", Set.of()), LocalDateTime.now(), LocalDateTime.now());

        when(userController.createUser(eq(userToken), any(CreateUserInputDataDTO.class))).thenReturn(output);

        ResponseEntity<UserOutputDTO> response = adapter.create(userToken, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
        verify(userController).createUser(userToken,
                new CreateUserInputDataDTO("John Doe", "jdoe@ex.com", "jdoe", "password123", userTypeId));
    }

    @Test
    void shouldReturnPageOfUsers() {
        var output = new UserOutputDTO(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe",
                new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of()), LocalDateTime.now(), LocalDateTime.now());
        var page = new PageOutputDTO<>(List.of(output), 0, 10, 1L, 1);

        when(userController.getAllUser(eq(userToken), any(PageInputDTO.class))).thenReturn(page);

        ResponseEntity<PageOutputDTO<UserOutputDTO>> response = adapter.getAll(userToken, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
        verify(userController).getAllUser(userToken, new PageInputDTO(0, 10));
    }

    @Test
    void shouldReturnUserById() {
        UUID id = UUID.randomUUID();
        var output = new UserOutputDTO(id, "John Doe", "jdoe@ex.com", "jdoe",
                new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of()), LocalDateTime.now(), LocalDateTime.now());

        when(userController.getUserById(userToken, id)).thenReturn(output);

        ResponseEntity<UserOutputDTO> response = adapter.getById(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void shouldUpdateUser() {
        UUID id = UUID.randomUUID();
        UUID userTypeId = UUID.randomUUID();
        var request = new UpdateUserRequest("John Updated", null, null, null, userTypeId);
        var output = new UserOutputDTO(id, "John Updated", "jdoe@ex.com", "jdoe",
                new UserTypeDTO(userTypeId, "Cliente", Set.of()), LocalDateTime.now(), LocalDateTime.now());

        when(userController.updateUser(eq(userToken), eq(id), any(UpdateUserInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<UserOutputDTO> response = adapter.update(userToken, id, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
        verify(userController).updateUser(userToken, id,
                new UpdateUserInputDataDTO("John Updated", null, null, null, userTypeId));
    }

    @Test
    void shouldDeleteUserAndReturn204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = adapter.delete(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userController, times(1)).deleteUser(userToken, id);
    }
}
