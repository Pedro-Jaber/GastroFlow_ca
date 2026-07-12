package com.group55.gastoflow_ca.core.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.UpdateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IMenuItemDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;

@ExtendWith(MockitoExtension.class)
class MenuItemControllerTest {

    @Mock
    private IMenuItemDataSource menuItemDataSource;

    @Mock
    private IRestaurantDataSource restaurantDataSource;

    private MenuItemController controller;

    private UserToken adminToken;

    @BeforeEach
    void setup() {
        controller = MenuItemController.create(menuItemDataSource, restaurantDataSource);

        UserType adminUserType = UserType.create(UUID.randomUUID(), "Admin", Set.of(Permission.values()));
        adminToken = new UserToken(UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", adminUserType);
    }

    private RestaurantDTO restaurantDTO(UUID id, UUID ownerId) {
        return new RestaurantDTO(id, "Sabor Caseiro", "Rua das Flores, 123", "Brasileira",
                "08:00 - 22:00", ownerId, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldCreateMenuItemSuccessfully() {
        UUID restaurantId = UUID.randomUUID();

        when(restaurantDataSource.findById(restaurantId))
                .thenReturn(Optional.of(restaurantDTO(restaurantId, UUID.randomUUID())));
        when(menuItemDataSource.saveNewMenuItem(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new CreateMenuItemInputDataDTO("Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId);

        MenuItemOutputDTO output = controller.createMenuItem(adminToken, input);

        assertThat(output.name()).isEqualTo("Feijoada Completa");
        assertThat(output.restaurantId()).isEqualTo(restaurantId);
        verify(menuItemDataSource, times(1)).saveNewMenuItem(any());
    }

    @Test
    void shouldThrowForbiddenWhenCreatingMenuItemWithoutPermission() {
        UUID restaurantId = UUID.randomUUID();
        UserType noPermissionType = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken tokenWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", noPermissionType);

        when(restaurantDataSource.findById(restaurantId))
                .thenReturn(Optional.of(restaurantDTO(restaurantId, UUID.randomUUID())));

        var input = new CreateMenuItemInputDataDTO("Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId);

        assertThatThrownBy(() -> controller.createMenuItem(tokenWithoutPermission, input))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void shouldReturnPageOfMenuItems() {
        var menuItemDTO = new MenuItemDTO(UUID.randomUUID(), "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        var pageInput = new PageInputDTO(0, 10);
        when(menuItemDataSource.findAll(pageInput))
                .thenReturn(new PageOutputDTO<>(List.of(menuItemDTO), 0, 10, 1L, 1));

        PageOutputDTO<MenuItemOutputDTO> result = controller.getAllMenuItems(adminToken, pageInput);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Feijoada Completa");
    }

    @Test
    void shouldReturnMenuItemById() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        var menuItemDTO = new MenuItemDTO(id, "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId, LocalDateTime.now(), LocalDateTime.now());

        when(menuItemDataSource.findById(id)).thenReturn(Optional.of(menuItemDTO));
        when(restaurantDataSource.findById(restaurantId))
                .thenReturn(Optional.of(restaurantDTO(restaurantId, UUID.randomUUID())));

        MenuItemOutputDTO result = controller.getMenuItemById(adminToken, id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Feijoada Completa");
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        var existingDTO = new MenuItemDTO(id, "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId, LocalDateTime.now(), LocalDateTime.now());

        when(menuItemDataSource.findById(id)).thenReturn(Optional.of(existingDTO));
        when(restaurantDataSource.findById(restaurantId))
                .thenReturn(Optional.of(restaurantDTO(restaurantId, UUID.randomUUID())));
        when(menuItemDataSource.updateMenuItem(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new UpdateMenuItemInputDataDTO("Feijoada Premium", null, null, null, null, null);
        MenuItemOutputDTO result = controller.updateMenuItem(adminToken, id, input);

        assertThat(result.name()).isEqualTo("Feijoada Premium");
        verify(menuItemDataSource, times(1)).updateMenuItem(any());
    }

    @Test
    void shouldDeleteMenuItem() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        var existingDTO = new MenuItemDTO(id, "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId, LocalDateTime.now(), LocalDateTime.now());

        when(menuItemDataSource.findById(id)).thenReturn(Optional.of(existingDTO));
        when(restaurantDataSource.findById(restaurantId))
                .thenReturn(Optional.of(restaurantDTO(restaurantId, UUID.randomUUID())));

        controller.deleteMenuItem(adminToken, id);

        verify(menuItemDataSource, times(1)).deleteById(id);
    }
}
