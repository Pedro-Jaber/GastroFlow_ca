package com.group55.gastoflow_ca.core.usecases.userType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class GetAllUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private GetAllUserTypeUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = GetAllUserTypeUseCase.create(userTypeGateway);
    }

    @Test
    void shouldReturnPageOfUserTypes() {
        var pageInput = new PageInputDTO(0, 10);

        var userType1 = UserType.create(UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.CREATE_RESTAURANT));
        var userType2 = UserType.create(UUID.randomUUID(), "Cliente", Set.of(Permission.CREATE_MENU_ITEM));

        var expectedPage = new PageOutputDTO<>(List.of(userType1, userType2), 0, 10, 2L, 1);

        when(userTypeGateway.findAll(pageInput)).thenReturn(expectedPage);

        var result = useCase.run(pageInput);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
    }

    @Test
    void shouldReturnEmptyPageWhenNoUserTypesExist() {
        var pageInput = new PageInputDTO(0, 10);
        var emptyPage = new PageOutputDTO<UserType>(List.of(), 0, 10, 0L, 0);

        when(userTypeGateway.findAll(pageInput)).thenReturn(emptyPage);

        var result = useCase.run(pageInput);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);
    }

    @Test
    void shouldRespectPageAndSizeParameters() {
        var pageInput = new PageInputDTO(2, 5);
        var userType = UserType.create(UUID.randomUUID(), "Gerente", Set.of(Permission.EDIT_RESTAURANT));
        var page = new PageOutputDTO<>(List.of(userType), 2, 5, 11L, 3);

        when(userTypeGateway.findAll(pageInput)).thenReturn(page);

        var result = useCase.run(pageInput);

        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(3);
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var pageInput = new PageInputDTO(0, 10);
        when(userTypeGateway.findAll(any())).thenReturn(new PageOutputDTO<>(List.of(), 0, 10, 0L, 0));

        useCase.run(pageInput);

        verify(userTypeGateway, times(1)).findAll(pageInput);
    }

    @Test
    void shouldPreserveUserTypeDataInPage() {
        var id = UUID.randomUUID();
        var permissions = Set.of(Permission.CREATE_RESTAURANT, Permission.EDIT_RESTAURANT);
        var userType = UserType.create(id, "Admin", permissions);
        var pageInput = new PageInputDTO(0, 10);

        when(userTypeGateway.findAll(pageInput))
                .thenReturn(new PageOutputDTO<>(List.of(userType), 0, 10, 1L, 1));

        var result = useCase.run(pageInput);

        var resultUserType = result.content().get(0);
        assertThat(resultUserType.getId()).isEqualTo(id);
        assertThat(resultUserType.getName()).isEqualTo("Admin");
        assertThat(resultUserType.getPermissions()).isEqualTo(permissions);
    }
}
