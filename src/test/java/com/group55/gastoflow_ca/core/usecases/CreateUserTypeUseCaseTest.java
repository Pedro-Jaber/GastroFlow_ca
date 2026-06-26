package com.group55.gastoflow_ca.core.usecases;

import java.util.Optional;
import java.util.Set;

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

import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.UserTypeAlreadyExistsException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;
import com.group55.gastoflow_ca.core.usecases.userType.CreateUserTypeUseCase;

@ExtendWith(MockitoExtension.class)
class CreateUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private CreateUserTypeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = CreateUserTypeUseCase.create(userTypeGateway);
    }

    @Test
    void shouldCreateUserTypeSuccessfully() {
        var permissions = Set.of(Permission.CREATE_RESTAURANT);
        var input = new CreateUserTypeInputDataDTO("Dono de Restaurante", permissions);
        var savedUserType = UserType.create("Dono de Restaurante", permissions);

        when(userTypeGateway.findByName("Dono de Restaurante")).thenReturn(Optional.empty());
        when(userTypeGateway.saveNewUserType(any(UserType.class))).thenReturn(savedUserType);

        var result = useCase.run(input);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Dono de Restaurante");
        assertThat(result.getPermissions()).isEqualTo(permissions);
        verify(userTypeGateway, times(1)).saveNewUserType(any(UserType.class));
    }

    @Test
    void shouldThrowWhenUserTypeNameAlreadyExists() {
        Set<Permission> permissions = Set.of(Permission.CREATE_RESTAURANT);
        var input = new CreateUserTypeInputDataDTO("Cliente", permissions);
        var existing = UserType.create("Cliente", permissions);

        when(userTypeGateway.findByName("Cliente")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(UserTypeAlreadyExistsException.class)
                .hasMessageContaining("Cliente");

        verify(userTypeGateway, never()).saveNewUserType(any());
    }

    @Test
    void shouldCheckForExistingNameBeforeSaving() {
        var input = new CreateUserTypeInputDataDTO("Gerente", Set.of());
        var saved = UserType.create("Gerente", Set.of());

        when(userTypeGateway.findByName("Gerente")).thenReturn(Optional.empty());
        when(userTypeGateway.saveNewUserType(any())).thenReturn(saved);

        useCase.run(input);

        var inOrder = inOrder(userTypeGateway);
        inOrder.verify(userTypeGateway).findByName("Gerente");
        inOrder.verify(userTypeGateway).saveNewUserType(any());
    }

    @Test
    void shouldPassCorrectPermissionsToGateway() {
        var permissions = Set.of(Permission.CREATE_RESTAURANT, Permission.EDIT_RESTAURANT);
        var input = new CreateUserTypeInputDataDTO("Gerente", permissions);
        var saved = UserType.create("Gerente", permissions);

        when(userTypeGateway.findByName(any())).thenReturn(Optional.empty());
        when(userTypeGateway.saveNewUserType(any())).thenReturn(saved);

        var result = useCase.run(input);

        assertThat(result.getPermissions())
                .containsExactlyInAnyOrderElementsOf(permissions);
    }
}