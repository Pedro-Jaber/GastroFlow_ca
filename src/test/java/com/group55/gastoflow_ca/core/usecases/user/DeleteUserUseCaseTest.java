package com.group55.gastoflow_ca.core.usecases.user;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    private DeleteUserUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = DeleteUserUseCase.create(userGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.DELETE_USER, Permission.DELETE_ALL_USER));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldDeleteUserById() {
        var id = UUID.randomUUID();

        useCase.run(requestingUserWithPermission, id);

        verify(userGateway, times(1)).deleteById(id);
    }

    @Test
    void shouldOnlyCallDeleteAndNothingElse() {
        var id = UUID.randomUUID();

        useCase.run(requestingUserWithPermission, id);

        verify(userGateway, times(1)).deleteById(id);
        verifyNoMoreInteractions(userGateway);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Client", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var id = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, id))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userGateway, never()).deleteById(id);
    }

    @Test
    void shouldAllowDeletingOwnAccountWithOnlyDeleteUserPermission() {
        UUID selfId = UUID.randomUUID();
        UserType selfType = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.DELETE_USER));
        UserToken selfToken = new UserToken(selfId, "John Doe", "jdoe@ex.com", "jdoe", selfType);

        useCase.run(selfToken, selfId);

        verify(userGateway, times(1)).deleteById(selfId);
    }

    @Test
    void shouldThrowForbiddenWhenDeletingOtherUserWithoutDeleteAllPermission() {
        UserType selfType = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.DELETE_USER));
        UserToken selfToken = new UserToken(
                UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", selfType);

        var otherId = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.run(selfToken, otherId))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userGateway, never()).deleteById(any());
    }
}
