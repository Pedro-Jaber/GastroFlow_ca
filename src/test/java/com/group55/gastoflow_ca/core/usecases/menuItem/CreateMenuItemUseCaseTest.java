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

import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

@ExtendWith(MockitoExtension.class)
class CreateMenuItemUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    @Mock
    private IRestaurantGateway restaurantGateway;

    private CreateMenuItemUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = CreateMenuItemUseCase.create(menuItemGateway, restaurantGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.CREATE_MENU_ITEM, Permission.CREATE_ALL_MENU_ITEM));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldCreateMenuItemSuccessfully() {
        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante A", "Rua 1", "Italiana", "08h-22h", UUID.randomUUID());
        var input = new CreateMenuItemInputDataDTO("Pizza", "desc", new BigDecimal("30.00"), true, "/pizza.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.saveNewMenuItem(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, input);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pizza");
        assertThat(result.getRestaurantId()).isEqualTo(restaurantId);
        verify(menuItemGateway, times(1)).saveNewMenuItem(any(MenuItem.class));
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        var restaurantId = UUID.randomUUID();
        var input = new CreateMenuItemInputDataDTO("Pizza", "desc", new BigDecimal("30.00"), true, "/pizza.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, input))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining(restaurantId.toString());

        verify(menuItemGateway, never()).saveNewMenuItem(any());
    }

    @Test
    void shouldCheckRestaurantExistenceBeforeSaving() {
        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante A", "Rua 1", "Italiana", "08h-22h", UUID.randomUUID());
        var input = new CreateMenuItemInputDataDTO("Suco", "desc", new BigDecimal("8.00"), false, "/suco.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.saveNewMenuItem(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.run(requestingUserWithPermission, input);

        var inOrder = org.mockito.Mockito.inOrder(restaurantGateway, menuItemGateway);
        inOrder.verify(restaurantGateway).findById(restaurantId);
        inOrder.verify(menuItemGateway).saveNewMenuItem(any());
    }

    @Test
    void shouldPassAllFieldsToCreatedMenuItem() {
        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante A", "Rua 1", "Italiana", "08h-22h", UUID.randomUUID());
        var input = new CreateMenuItemInputDataDTO("Lasanha", "Com carne", new BigDecimal("45.00"), true,
                "/lasanha.png", restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.saveNewMenuItem(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, input);

        assertThat(result.getDescription()).isEqualTo("Com carne");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("45.00"));
        assertThat(result.isOnlyInRestaurant()).isTrue();
        assertThat(result.getPhotoPath()).isEqualTo("/lasanha.png");
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante A", "Rua 1", "Italiana", "08h-22h", UUID.randomUUID());
        var input = new CreateMenuItemInputDataDTO("Pizza", "desc", new BigDecimal("30.00"), true, "/pizza.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).saveNewMenuItem(any());
    }

    @Test
    void shouldAllowOwnerToCreateMenuItemForOwnRestaurantWithOnlyCreateMenuItemPermission() {
        UUID ownerId = UUID.randomUUID();
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.CREATE_MENU_ITEM));
        UserToken ownerToken = new UserToken(ownerId, "Dono", "dono@ex.com", "dono", ownerType);

        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h", ownerId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new CreateMenuItemInputDataDTO("Pizza", "desc", new BigDecimal("30.00"), true, "/pizza.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.saveNewMenuItem(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(ownerToken, input);

        assertThat(result.getName()).isEqualTo("Pizza");
    }

    @Test
    void shouldThrowForbiddenWhenCreatingMenuItemForOtherRestaurantWithoutCreateAllPermission() {
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.CREATE_MENU_ITEM));
        UserToken ownerToken = new UserToken(
                UUID.randomUUID(), "Dono", "dono@ex.com", "dono", ownerType);

        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create(restaurantId, "Restaurante A", "Rua 1", "Italiana", "08h-22h",
                UUID.randomUUID(), java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new CreateMenuItemInputDataDTO("Pizza", "desc", new BigDecimal("30.00"), true, "/pizza.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(ownerToken, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(menuItemGateway, never()).saveNewMenuItem(any());
    }
}
