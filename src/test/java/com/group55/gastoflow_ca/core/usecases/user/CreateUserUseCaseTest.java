package com.group55.gastoflow_ca.core.usecases.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IUserTypeGateway userTypeGateway;

    private CreateUserUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = CreateUserUseCase.create(userGateway, userTypeGateway);
    }

    @DisplayName("Test Create User Use Case")
    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String name = "John Doe";
        String emailAddress = "jdoe@ex.com";
        String login = "jdoe";
        String password = "password123";
        UserType userType = UserType.create(UUID.randomUUID(), "Admin", null);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        when(userGateway.findByLogin(anyString())).thenReturn(Optional.empty());
        when(userTypeGateway.findById(any())).thenReturn(Optional.of(userType));
        when(userGateway.saveNewUser(any()))
                .thenReturn(
                        User.create(userId, name, emailAddress, login, password, userType, createdAt, updatedAt));

        // Act
        User user = useCase.run(new CreateUserInputDataDTO(name, emailAddress, login, password, userType.getId()));

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(user.getLogin()).isEqualTo(login);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getUserType()).isEqualTo(userType);
        verify(userGateway, times(1)).saveNewUser(any());
    }

    @Test
    void shouldThrowWhenLoginAlreadyExists() {
        var userType = UserType.create(UUID.randomUUID(), "Admin", null);
        var existingUser = User.create(UUID.randomUUID(), "Jane Doe", "jane@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());
        var input = new CreateUserInputDataDTO("John Doe", "jdoe@ex.com", "jdoe", "password123", userType.getId());

        when(userGateway.findByLogin("jdoe")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("jdoe");

        verify(userGateway, never()).saveNewUser(any());
        verify(userTypeGateway, never()).findById(any());
    }

    @Test
    void shouldThrowWhenUserTypeDoesNotExist() {
        UUID missingUserTypeId = UUID.randomUUID();
        var input = new CreateUserInputDataDTO("John Doe", "jdoe@ex.com", "jdoe", "password123", missingUserTypeId);

        when(userGateway.findByLogin("jdoe")).thenReturn(Optional.empty());
        when(userTypeGateway.findById(missingUserTypeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(UserTypeNotFoundException.class)
                .hasMessageContaining(missingUserTypeId.toString());

        verify(userGateway, never()).saveNewUser(any());
    }

    @Test
    void shouldCheckLoginBeforeCheckingUserTypeAndSaving() {
        var userType = UserType.create(UUID.randomUUID(), "Admin", null);
        var input = new CreateUserInputDataDTO("John Doe", "jdoe@ex.com", "jdoe", "password123", userType.getId());

        when(userGateway.findByLogin("jdoe")).thenReturn(Optional.empty());
        when(userTypeGateway.findById(userType.getId())).thenReturn(Optional.of(userType));
        when(userGateway.saveNewUser(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.run(input);

        var order = inOrder(userGateway, userTypeGateway);
        order.verify(userGateway).findByLogin("jdoe");
        order.verify(userTypeGateway).findById(userType.getId());
        order.verify(userGateway).saveNewUser(any());
    }
}
