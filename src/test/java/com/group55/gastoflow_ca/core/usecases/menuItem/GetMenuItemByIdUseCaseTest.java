package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
class GetMenuItemByIdUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    private GetMenuItemByIdUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = GetMenuItemByIdUseCase.create(menuItemGateway, restaurantGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.READ_MENU_ITEM, Permission.READ_ALL_MENU_ITEM));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldReturnMenuItemWhenFound() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h",
                UUID.randomUUID(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        var result = useCase.run(requestingUserWithPermission, id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Pizza");
    }

    @Test
    void shouldThrowWhenMenuItemNotFound() {
        var id = UUID.randomUUID();

        when(menuItemGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create(id, "Item", "desc", BigDecimal.ONE, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h",
                UUID.randomUUID(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        useCase.run(requestingUserWithPermission, id);

        verify(menuItemGateway, times(1)).findById(id);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Item", "desc", BigDecimal.ONE, true, "/a.png", restaurantId);
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h",
                UUID.randomUUID(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, menuItem.getId()))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void shouldAllowOwnerToReadMenuItemOfOwnRestaurantWithOnlyReadMenuItemPermission() {
        UUID ownerId = UUID.randomUUID();
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.READ_MENU_ITEM));
        UserToken ownerToken = new UserToken(ownerId, "Dono", "dono@ex.com", "dono", ownerType);

        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h", ownerId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        var result = useCase.run(ownerToken, menuItem.getId());

        assertThat(result.getId()).isEqualTo(menuItem.getId());
    }

    @Test
    void shouldThrowForbiddenWhenReadingMenuItemOfOtherRestaurantWithoutReadAllPermission() {
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.READ_MENU_ITEM));
        UserToken ownerToken = new UserToken(
                UUID.randomUUID(), "Dono", "dono@ex.com", "dono", ownerType);

        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create("Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h",
                UUID.randomUUID(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(menuItem.getId())).thenReturn(Optional.of(menuItem));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(ownerToken, menuItem.getId()))
                .isInstanceOf(ForbiddenActionException.class);
    }
}
