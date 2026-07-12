package com.group55.gastoflow_ca.core.usecases.auth;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.InvalidCredentialsException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IPasswordHasher passwordHasher;

    private LoginUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = LoginUseCase.create(userGateway, passwordHasher);
    }

    @Test
    void shouldReturnUserTokenWhenCredentialsAreValid() {
        var userType = UserType.create(UUID.randomUUID(), "Admin", Set.of(Permission.READ_USER));
        var user = User.create(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "encoded-password",
                userType, LocalDateTime.now(), LocalDateTime.now());
        var input = new LoginInputDataDTO("jdoe", "raw-password");

        when(userGateway.findByLogin("jdoe")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("raw-password", "encoded-password")).thenReturn(true);

        UserToken result = useCase.run(input);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNome()).isEqualTo(user.getName());
        assertThat(result.getEmailAddress()).isEqualTo(user.getEmailAddress());
        assertThat(result.getLogin()).isEqualTo(user.getLogin());
        assertThat(result.getUserType()).isEqualTo(userType);
    }

    @Test
    void shouldThrowInvalidCredentialsWhenLoginDoesNotExist() {
        var input = new LoginInputDataDTO("unknown", "any-password");

        when(userGateway.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(passwordHasher, never()).matches(any(), any());
    }

    @Test
    void shouldThrowInvalidCredentialsWhenPasswordDoesNotMatch() {
        var userType = UserType.create(UUID.randomUUID(), "Admin", Set.of());
        var user = User.create(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "encoded-password",
                userType, LocalDateTime.now(), LocalDateTime.now());
        var input = new LoginInputDataDTO("jdoe", "wrong-password");

        when(userGateway.findByLogin("jdoe")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void shouldNotLeakWhetherLoginExistsOnInvalidCredentials() {
        var missingLoginInput = new LoginInputDataDTO("unknown", "any-password");
        when(userGateway.findByLogin("unknown")).thenReturn(Optional.empty());

        var userType = UserType.create(UUID.randomUUID(), "Admin", Set.of());
        var user = User.create(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "encoded-password",
                userType, LocalDateTime.now(), LocalDateTime.now());
        var wrongPasswordInput = new LoginInputDataDTO("jdoe", "wrong-password");
        when(userGateway.findByLogin("jdoe")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong-password", "encoded-password")).thenReturn(false);

        String messageForMissingLogin = catchMessage(missingLoginInput);
        String messageForWrongPassword = catchMessage(wrongPasswordInput);

        assertThat(messageForMissingLogin).isEqualTo(messageForWrongPassword);
    }

    @Test
    void shouldCheckLoginBeforeCheckingPassword() {
        var input = new LoginInputDataDTO("unknown", "any-password");

        when(userGateway.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userGateway, times(1)).findByLogin("unknown");
        verify(passwordHasher, never()).matches(any(), any());
    }

    private String catchMessage(LoginInputDataDTO input) {
        try {
            useCase.run(input);
            throw new IllegalStateException("Expected InvalidCredentialsException to be thrown");
        } catch (InvalidCredentialsException e) {
            return e.getMessage();
        }
    }
}
