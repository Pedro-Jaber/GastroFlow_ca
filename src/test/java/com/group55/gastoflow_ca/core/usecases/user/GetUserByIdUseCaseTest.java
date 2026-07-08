package com.group55.gastoflow_ca.core.usecases.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    private GetUserByIdUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = GetUserByIdUseCase.create(userGateway);
    }

    @Test
    void shouldReturnUserWhenFound() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var user = User.create(id, "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());

        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        var result = useCase.run(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var id = UUID.randomUUID();

        when(userGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var user = User.create(id, "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());

        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        useCase.run(id);

        verify(userGateway, times(1)).findById(id);
    }
}
