package com.group55.gastoflow_ca.core.usecases.userType;

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

import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class GetUserTypeByIdUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private GetUserTypeByIdUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = GetUserTypeByIdUseCase.create(userTypeGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.READ_USERTYPE));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldReturnUserTypeWhenFound() {
        var id = UUID.randomUUID();
        var userType = UserType.create(id, "Cliente", Set.of(Permission.CREATE_MENU_ITEM));

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        var result = useCase.run(requestingUserWithPermission, id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Cliente");
        assertThat(result.getPermissions()).isEqualTo(Set.of(Permission.CREATE_MENU_ITEM));
    }

    @Test
    void shouldThrowWhenUserTypeNotFound() {
        var id = UUID.randomUUID();

        when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id))
                .isInstanceOf(UserTypeNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var id = UUID.randomUUID();
        var userType = UserType.create(id, "Gerente", Set.of());

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        useCase.run(requestingUserWithPermission, id);

        verify(userTypeGateway, times(1)).findById(id);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var id = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, id))
                .isInstanceOf(ForbiddenActionException.class);

        verify(userTypeGateway, never()).findById(any());
    }
}
