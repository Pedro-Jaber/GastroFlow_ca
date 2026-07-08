package com.group55.gastoflow_ca.core.usecases.user;

import java.time.LocalDateTime;
import java.util.Optional;
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

import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IUserTypeGateway userTypeGateway;

    private UpdateUserUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = UpdateUserUseCase.create(userGateway, userTypeGateway);
    }

    private User existingUser(UUID id, UserType userType) {
        return User.create(id, "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldUpdateNameEmailAndLoginSuccessfully() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var input = new UpdateUserInputDataDTO("Jane Doe", "jane@ex.com", "jane", null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmailAddress()).isEqualTo("jane@ex.com");
        assertThat(result.getLogin()).isEqualTo("jane");
        verify(userGateway, times(1)).updateUser(any(User.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var id = UUID.randomUUID();
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(id, input))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(userGateway, never()).updateUser(any());
    }

    @Test
    void shouldKeepExistingFieldsWhenInputFieldsAreNullOrBlank() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var input = new UpdateUserInputDataDTO(null, "   ", null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmailAddress()).isEqualTo("jdoe@ex.com");
        assertThat(result.getLogin()).isEqualTo("jdoe");
    }

    @Test
    void shouldUpdateUserTypeWhenUserTypeIdIsProvided() {
        var id = UUID.randomUUID();
        var oldUserType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var newUserType = UserType.create(UUID.randomUUID(), "Dono de Restaurante", null);
        var existing = existingUser(id, oldUserType);
        var input = new UpdateUserInputDataDTO(null, null, null, newUserType.getId());

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.findById(newUserType.getId())).thenReturn(Optional.of(newUserType));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getUserType()).isEqualTo(newUserType);
    }

    @Test
    void shouldThrowWhenUserTypeIdProvidedDoesNotExist() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var missingUserTypeId = UUID.randomUUID();
        var input = new UpdateUserInputDataDTO(null, null, null, missingUserTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.findById(missingUserTypeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(id, input))
                .isInstanceOf(UserTypeNotFoundException.class)
                .hasMessageContaining(missingUserTypeId.toString());

        verify(userGateway, never()).updateUser(any());
    }

    @Test
    void shouldUpdateTheUpdatedAtTimestamp() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var oldUpdatedAt = LocalDateTime.now().minusDays(1);
        var existing = User.create(id, "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, oldUpdatedAt, oldUpdatedAt);
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getUpdatedAt()).isAfter(oldUpdatedAt);
    }
}
