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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.usertype.UpdateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.UserTypeNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class UpdateUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private UpdateUserTypeUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = UpdateUserTypeUseCase.create(userTypeGateway);
    }

    @Test
    void shouldUpdateNameAndPermissionsSuccessfully() {
        var id = UUID.randomUUID();
        var existing = UserType.create(id, "Cliente", Set.of(Permission.CREATE_MENU_ITEM));
        var input = new UpdateUserTypeInputDataDTO("Dono de Restaurante", Set.of(Permission.CREATE_RESTAURANT));

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.updateUserType(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getName()).isEqualTo("Dono de Restaurante");
        assertThat(result.getPermissions()).isEqualTo(Set.of(Permission.CREATE_RESTAURANT));
        verify(userTypeGateway, times(1)).updateUserType(any(UserType.class));
    }

    @Test
    void shouldThrowWhenUserTypeNotFound() {
        var id = UUID.randomUUID();
        var input = new UpdateUserTypeInputDataDTO("Cliente", Set.of());

        when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(id, input))
                .isInstanceOf(UserTypeNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(userTypeGateway, never()).updateUserType(any());
    }

    @Test
    void shouldKeepExistingNameWhenInputNameIsNull() {
        var id = UUID.randomUUID();
        var existing = UserType.create(id, "Cliente", Set.of(Permission.CREATE_MENU_ITEM));
        var input = new UpdateUserTypeInputDataDTO(null, Set.of(Permission.CREATE_RESTAURANT));

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.updateUserType(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getName()).isEqualTo("Cliente");
        assertThat(result.getPermissions()).isEqualTo(Set.of(Permission.CREATE_RESTAURANT));
    }

    @Test
    void shouldKeepExistingNameWhenInputNameIsBlank() {
        var id = UUID.randomUUID();
        var existing = UserType.create(id, "Cliente", Set.of(Permission.CREATE_MENU_ITEM));
        var input = new UpdateUserTypeInputDataDTO("   ", null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.updateUserType(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getName()).isEqualTo("Cliente");
        assertThat(result.getPermissions()).isEqualTo(Set.of(Permission.CREATE_MENU_ITEM));
    }

    @Test
    void shouldKeepExistingPermissionsWhenInputPermissionsIsNull() {
        var id = UUID.randomUUID();
        var existing = UserType.create(id, "Cliente", Set.of(Permission.CREATE_MENU_ITEM));
        var input = new UpdateUserTypeInputDataDTO("Cliente Premium", null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.updateUserType(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(id, input);

        assertThat(result.getName()).isEqualTo("Cliente Premium");
        assertThat(result.getPermissions()).isEqualTo(Set.of(Permission.CREATE_MENU_ITEM));
    }

    @Test
    void shouldCheckExistenceBeforeUpdating() {
        var id = UUID.randomUUID();
        var existing = UserType.create(id, "Cliente", Set.of());
        var input = new UpdateUserTypeInputDataDTO("Gerente", Set.of(Permission.EDIT_RESTAURANT));

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userTypeGateway.updateUserType(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.run(id, input);

        var inOrder = inOrder(userTypeGateway);
        inOrder.verify(userTypeGateway).findById(id);
        inOrder.verify(userTypeGateway).updateUserType(any());
    }
}
