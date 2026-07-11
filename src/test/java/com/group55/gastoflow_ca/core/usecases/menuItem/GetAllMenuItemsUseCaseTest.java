package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.math.BigDecimal;
import java.util.List;
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

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

@ExtendWith(MockitoExtension.class)
class GetAllMenuItemsUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    private GetAllMenuItemsUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = GetAllMenuItemsUseCase.create(menuItemGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.READ_ALL_MENU_ITEM));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldReturnPageOfMenuItems() {
        var pageInput = new PageInputDTO(0, 10);
        var restaurantId = UUID.randomUUID();

        var item1 = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);
        var item2 = MenuItem.create("Suco", "desc", BigDecimal.ONE, false, "/b.png", restaurantId);

        var expectedPage = new PageOutputDTO<>(List.of(item1, item2), 0, 10, 2L, 1);

        when(menuItemGateway.findAll(pageInput)).thenReturn(expectedPage);

        var result = useCase.run(requestingUserWithPermission, pageInput);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyPageWhenNoMenuItemsExist() {
        var pageInput = new PageInputDTO(0, 10);
        var emptyPage = new PageOutputDTO<MenuItem>(List.of(), 0, 10, 0L, 0);

        when(menuItemGateway.findAll(pageInput)).thenReturn(emptyPage);

        var result = useCase.run(requestingUserWithPermission, pageInput);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);
    }

    @Test
    void shouldRespectPageAndSizeParameters() {
        var pageInput = new PageInputDTO(1, 5);
        var restaurantId = UUID.randomUUID();
        var item = MenuItem.create("Item", "desc", BigDecimal.ONE, true, "/a.png", restaurantId);
        var page = new PageOutputDTO<>(List.of(item), 1, 5, 6L, 2);

        when(menuItemGateway.findAll(pageInput)).thenReturn(page);

        var result = useCase.run(requestingUserWithPermission, pageInput);

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(2);
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var pageInput = new PageInputDTO(0, 10);
        when(menuItemGateway.findAll(any())).thenReturn(new PageOutputDTO<>(List.of(), 0, 10, 0L, 0));

        useCase.run(requestingUserWithPermission, pageInput);

        verify(menuItemGateway, times(1)).findAll(pageInput);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var pageInput = new PageInputDTO(0, 10);

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, pageInput))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).findAll(any());
    }
}
