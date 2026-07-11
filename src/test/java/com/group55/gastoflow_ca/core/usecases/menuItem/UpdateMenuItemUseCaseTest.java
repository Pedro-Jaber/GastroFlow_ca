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
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.menu_item.UpdateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

@ExtendWith(MockitoExtension.class)
class UpdateMenuItemUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    private UpdateMenuItemUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = UpdateMenuItemUseCase.create(menuItemGateway, restaurantGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.EDIT_MENU_ITEM, Permission.EDIT_ALL_MENU_ITEM));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    private Restaurant anyRestaurant(UUID id) {
        return Restaurant.create(id, "Restaurante", "Rua", "Tipo", "Horário", UUID.randomUUID(),
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
    }

    @Test
    void shouldUpdateAllFieldsSuccessfully() {
        var id = UUID.randomUUID();
        var oldRestaurantId = UUID.randomUUID();
        var newRestaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc antiga", BigDecimal.TEN, true, "/old.png", oldRestaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var newRestaurant = Restaurant.create(newRestaurantId, "Novo Restaurante", "Rua 2", "Japonesa", "10h-20h",
                UUID.randomUUID(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        var input = new UpdateMenuItemInputDataDTO("Sushi", "desc nova", new BigDecimal("50.00"), false, "/new.png",
                newRestaurantId);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(oldRestaurantId)).thenReturn(Optional.of(anyRestaurant(oldRestaurantId)));
        when(restaurantGateway.findById(newRestaurantId)).thenReturn(Optional.of(newRestaurant));
        when(menuItemGateway.updateMenuItem(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getName()).isEqualTo("Sushi");
        assertThat(result.getDescription()).isEqualTo("desc nova");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(result.isOnlyInRestaurant()).isFalse();
        assertThat(result.getPhotoPath()).isEqualTo("/new.png");
        assertThat(result.getRestaurantId()).isEqualTo(newRestaurantId);
        verify(menuItemGateway, times(1)).updateMenuItem(any(MenuItem.class));
    }

    @Test
    void shouldThrowWhenMenuItemNotFound() {
        var id = UUID.randomUUID();
        var input = new UpdateMenuItemInputDataDTO("Sushi", null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(menuItemGateway, never()).updateMenuItem(any());
    }

    @Test
    void shouldThrowWhenNewRestaurantNotFound() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var newRestaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateMenuItemInputDataDTO(null, null, null, null, null, newRestaurantId);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(anyRestaurant(restaurantId)));
        when(restaurantGateway.findById(newRestaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining(newRestaurantId.toString());

        verify(menuItemGateway, never()).updateMenuItem(any());
    }

    @Test
    void shouldThrowWhenCurrentRestaurantNotFound() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateMenuItemInputDataDTO(null, null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
                .isInstanceOf(RestaurantNotFoundException.class);

        verify(menuItemGateway, never()).updateMenuItem(any());
    }

    @Test
    void shouldKeepExistingValuesWhenInputFieldsAreNull() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc", new BigDecimal("20.00"), true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateMenuItemInputDataDTO(null, null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(anyRestaurant(restaurantId)));
        when(menuItemGateway.updateMenuItem(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getName()).isEqualTo("Pizza");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("20.00"));
        assertThat(result.getRestaurantId()).isEqualTo(restaurantId);
        verify(restaurantGateway, times(1)).findById(any());
    }

    @Test
    void shouldUpdateUpdatedAtTimestamp() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var oldUpdatedAt = java.time.LocalDateTime.now().minusDays(2);
        var existing = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now().minusDays(3), oldUpdatedAt);
        var input = new UpdateMenuItemInputDataDTO("Pizza Nova", null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(anyRestaurant(restaurantId)));
        when(menuItemGateway.updateMenuItem(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getUpdatedAt()).isAfter(oldUpdatedAt);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateMenuItemInputDataDTO("Nome Novo", null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(anyRestaurant(restaurantId)));

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, id, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).updateMenuItem(any());
    }

    @Test
    void shouldAllowOwnerToUpdateMenuItemOfOwnRestaurantWithOnlyEditMenuItemPermission() {
        UUID ownerId = UUID.randomUUID();
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.EDIT_MENU_ITEM));
        UserToken ownerToken = new UserToken(ownerId, "Dono", "dono@ex.com", "dono", ownerType);

        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var restaurant = Restaurant.create(restaurantId, "Restaurante", "Rua", "Tipo", "Horário", ownerId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateMenuItemInputDataDTO("Nome Novo", null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.updateMenuItem(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.run(ownerToken, id, input);

        assertThat(result.getName()).isEqualTo("Nome Novo");
    }

    @Test
    void shouldThrowForbiddenWhenUpdatingMenuItemOfOtherRestaurantWithoutEditAllPermission() {
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.EDIT_MENU_ITEM));
        UserToken ownerToken = new UserToken(
                UUID.randomUUID(), "Dono", "dono@ex.com", "dono", ownerType);

        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var existing = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var restaurant = Restaurant.create(restaurantId, "Restaurante", "Rua", "Tipo", "Horário", UUID.randomUUID(),
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateMenuItemInputDataDTO("Nome Novo", null, null, null, null, null);

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(ownerToken, id, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).updateMenuItem(any());
    }
}
