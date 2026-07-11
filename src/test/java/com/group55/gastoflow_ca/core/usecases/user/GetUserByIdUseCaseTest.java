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
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    private GetUserByIdUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = GetUserByIdUseCase.create(userGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.READ_USER, Permission.READ_ALL_USER));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldReturnUserWhenFound() {
        var id = UUID.randomUUID();
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var user = User.create(id, "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());

        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        var result = useCase.run(requestingUserWithPermission, id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        var id = UUID.randomUUID();

        when(userGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id))
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

        useCase.run(requestingUserWithPermission, id);

        verify(userGateway, times(1)).findById(id);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Client", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var id = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, id))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userGateway, never()).findById(any());
    }

    @Test
    void shouldAllowReadingOwnDataWithOnlyReadUserPermission() {
        UserType selfType = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.READ_USER));
        UUID selfId = UUID.randomUUID();
        UserToken selfToken = new UserToken(selfId, "John Doe", "jdoe@ex.com", "jdoe", selfType);

        var user = User.create(selfId, "John Doe", "jdoe@ex.com", "jdoe", "123",
                selfType, LocalDateTime.now(), LocalDateTime.now());

        when(userGateway.findById(selfId)).thenReturn(Optional.of(user));

        var result = useCase.run(selfToken, selfId);

        assertThat(result.getId()).isEqualTo(selfId);
    }

    @Test
    void shouldThrowForbiddenWhenReadingOtherUserWithoutReadAllPermission() {
        UserType selfType = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.READ_USER));
        UserToken selfToken = new UserToken(
                UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", selfType);

        var otherId = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.run(selfToken, otherId))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userGateway, never()).findById(any());
    }
}
