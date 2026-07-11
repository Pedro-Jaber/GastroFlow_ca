package com.group55.gastoflow_ca.core.usecases.user;

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
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    @Mock
    private IUserTypeGateway userTypeGateway;

    @Mock
    private IPasswordHasher passwordHasher;

    private UpdateUserUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = UpdateUserUseCase.create(userGateway, userTypeGateway, passwordHasher);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.EDIT_USER, Permission.EDIT_ALL_USER));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
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
        var input = new UpdateUserInputDataDTO("Jane Doe", "jane@ex.com", "jane", null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmailAddress()).isEqualTo("jane@ex.com");
        assertThat(result.getLogin()).isEqualTo("jane");
        verify(userGateway, times(1)).updateUser(any(User.class));
        verify(passwordHasher, never()).encode(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var id = UUID.randomUUID();
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(userGateway, never()).updateUser(any());
        verify(passwordHasher, never()).encode(any());
    }

    @Test
    void shouldKeepExistingFieldsWhenInputFieldsAreNullOrBlank() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var input = new UpdateUserInputDataDTO(null, "   ", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmailAddress()).isEqualTo("jdoe@ex.com");
        assertThat(result.getLogin()).isEqualTo("jdoe");
        assertThat(result.getPassword()).isEqualTo("123");
    }

    @Test
    void shouldUpdateUserTypeWhenUserTypeIdIsProvided() {
        var id = UUID.randomUUID();
        var oldUserType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var newUserType = UserType.create(UUID.randomUUID(), "Dono de Restaurante", null);
        var existing = existingUser(id, oldUserType);
        var input = new UpdateUserInputDataDTO(null, null, null, null, newUserType.getId());

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.findById(newUserType.getId())).thenReturn(Optional.of(newUserType));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getUserType()).isEqualTo(newUserType);
    }

    @Test
    void shouldThrowWhenUserTypeIdProvidedDoesNotExist() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var missingUserTypeId = UUID.randomUUID();
        var input = new UpdateUserInputDataDTO(null, null, null, null, missingUserTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.findById(missingUserTypeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
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
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getUpdatedAt()).isAfter(oldUpdatedAt);
    }

    @Test
    void shouldEncodePasswordWhenProvided() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var input = new UpdateUserInputDataDTO(null, null, null, "new-raw-password", null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(passwordHasher.encode("new-raw-password")).thenReturn("hashed-new-password");
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getPassword()).isEqualTo("hashed-new-password");
        verify(passwordHasher, times(1)).encode("new-raw-password");
    }

    @Test
    void shouldNotEncodePasswordWhenNullOrBlank() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var input = new UpdateUserInputDataDTO(null, null, null, "   ", null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getPassword()).isEqualTo("123");
        verify(passwordHasher, never()).encode(anyString());
    }

    @Test
    void shouldNeverPersistRawPasswordWhenUpdating() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var existing = existingUser(id, userType);
        var input = new UpdateUserInputDataDTO(null, null, null, "super-secret", null);

        when(userGateway.findById(id)).thenReturn(Optional.of(existing));
        when(passwordHasher.encode("super-secret")).thenReturn("$2a$10$fakehashvalue");
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getPassword()).isEqualTo("$2a$10$fakehashvalue");
        assertThat(result.getPassword()).isNotEqualTo("super-secret");
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Client", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var id = UUID.randomUUID();
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null, null);

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, id, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userGateway, never()).findById(any());
        verify(userGateway, never()).updateUser(any());
        verify(passwordHasher, never()).encode(any());
    }

    @Test
    void shouldAllowUpdatingOwnDataWithOnlyEditUserPermission() {
        UUID selfId = UUID.randomUUID();
        UserType selfType = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.EDIT_USER));
        UserToken selfToken = new UserToken(selfId, "John Doe", "jdoe@ex.com", "jdoe", selfType);

        var existing = existingUser(selfId, selfType);
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null, null);

        when(userGateway.findById(selfId)).thenReturn(Optional.of(existing));
        when(userGateway.updateUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(selfToken, selfId, input);

        assertThat(result.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void shouldThrowForbiddenWhenUpdatingOtherUserWithoutEditAllPermission() {
        UserType selfType = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.EDIT_USER));
        UserToken selfToken = new UserToken(
                UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", selfType);

        var otherId = UUID.randomUUID();
        var input = new UpdateUserInputDataDTO("Jane Doe", null, null, null, null);

        assertThatThrownBy(() -> useCase.run(selfToken, otherId, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userGateway, never()).findById(any());
        verify(userGateway, never()).updateUser(any());
    }
}
