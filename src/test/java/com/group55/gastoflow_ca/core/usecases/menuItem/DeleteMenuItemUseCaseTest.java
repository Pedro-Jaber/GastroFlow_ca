package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.math.BigDecimal;
import java.util.Optional;
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

@ExtendWith(MockitoExtension.class)
class DeleteMenuItemUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    private DeleteMenuItemUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = DeleteMenuItemUseCase.create(menuItemGateway, restaurantGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.DELETE_MENU_ITEM, Permission.DELETE_ALL_MENU_ITEM));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    private Restaurant anyRestaurant(UUID id) {
        return Restaurant.create(id, "Restaurante", "Rua", "Tipo", "Horário", UUID.randomUUID(),
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
    }

    @Test
    void shouldDeleteMenuItemById() {
        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(anyRestaurant(restaurantId)));

        useCase.run(requestingUserWithPermission, menuItem.getId());

        verify(menuItemGateway, times(1)).deleteById(menuItem.getId());
    }

    @Test
    void shouldThrowWhenMenuItemNotFound() {
        var id = UUID.randomUUID();

        when(menuItemGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(menuItemGateway, never()).deleteById(any());
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(anyRestaurant(restaurantId)));

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, menuItem.getId()))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).deleteById(any());
    }

    @Test
    void shouldAllowOwnerToDeleteMenuItemOfOwnRestaurantWithOnlyDeleteMenuItemPermission() {
        UUID ownerId = UUID.randomUUID();
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.DELETE_MENU_ITEM));
        UserToken ownerToken = new UserToken(ownerId, "Dono", "dono@ex.com", "dono", ownerType);

        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);
        var restaurant = Restaurant.create(restaurantId, "Restaurante", "Rua", "Tipo", "Horário", ownerId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        useCase.run(ownerToken, menuItem.getId());

        verify(menuItemGateway, times(1)).deleteById(menuItem.getId());
    }

    @Test
    void shouldThrowForbiddenWhenDeletingMenuItemOfOtherRestaurantWithoutDeleteAllPermission() {
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.DELETE_MENU_ITEM));
        UserToken ownerToken = new UserToken(
                UUID.randomUUID(), "Dono", "dono@ex.com", "dono", ownerType);

        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);
        var restaurant = Restaurant.create(restaurantId, "Restaurante", "Rua", "Tipo", "Horário", UUID.randomUUID(),
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(ownerToken, menuItem.getId()))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).deleteById(any());
    }
}
