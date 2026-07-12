package com.group55.gastoflow_ca.core.controllers;

import java.time.LocalDateTime;
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.InvalidCredentialsException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IUserDataSource userDataSource;

    @Mock
    private IPasswordHasher passwordHasher;

    private AuthController controller;

    @BeforeEach
    void setup() {
        controller = AuthController.create(userDataSource, passwordHasher);
    }

    @Test
    void shouldLoginSuccessfullyAndReturnUserToken() {
        UUID userId = UUID.randomUUID();
        UserTypeDTO userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of(Permission.READ_USER));
        UserDTO userDTO = new UserDTO(userId, "John Doe", "jdoe@ex.com", "jdoe", "encoded-pass",
                userTypeDTO, LocalDateTime.now(), LocalDateTime.now());

        when(userDataSource.findByLogin("jdoe")).thenReturn(Optional.of(userDTO));
        when(passwordHasher.matches("password123", "encoded-pass")).thenReturn(true);

        UserToken result = controller.login(new LoginInputDataDTO("jdoe", "password123"));

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getLogin()).isEqualTo("jdoe");
        assertThat(result.getUserType().getName()).isEqualTo("Cliente");
    }

    @Test
    void shouldThrowInvalidCredentialsWhenLoginDoesNotExist() {
        when(userDataSource.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.login(new LoginInputDataDTO("unknown", "password123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void shouldThrowInvalidCredentialsWhenPasswordIsWrong() {
        UserTypeDTO userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "Cliente", Set.of());
        UserDTO userDTO = new UserDTO(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "encoded-pass",
                userTypeDTO, LocalDateTime.now(), LocalDateTime.now());

        when(userDataSource.findByLogin("jdoe")).thenReturn(Optional.of(userDTO));
        when(passwordHasher.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> controller.login(new LoginInputDataDTO("jdoe", "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
