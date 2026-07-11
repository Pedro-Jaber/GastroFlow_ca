package com.group55.gastoflow_ca.core.usecases.restaurant;

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

import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

@ExtendWith(MockitoExtension.class)
class GetRestaurantByIdUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    private GetRestaurantByIdUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = GetRestaurantByIdUseCase.create(restaurantGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.READ_RESTAURANT, Permission.READ_ALL_RESTAURANT));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldReturnRestaurantWhenFound() {
        var ownerId = UUID.randomUUID();
        var restaurant = Restaurant.create("Bar do Zé", "Rua A", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        var result = useCase.run(requestingUserWithPermission, restaurant.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurant.getId());
        assertThat(result.getName()).isEqualTo("Bar do Zé");
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        var id = UUID.randomUUID();

        when(restaurantGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var id = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante", "Endereço", "Japonesa", "10:00-22:00", UUID.randomUUID());

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(restaurant));

        useCase.run(requestingUserWithPermission, id);

        verify(restaurantGateway, times(1)).findById(id);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var restaurant = Restaurant.create("Restaurante", "Endereço", "Japonesa", "10:00-22:00", UUID.randomUUID());

        when(restaurantGateway.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, restaurant.getId()))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void shouldAllowOwnerToReadOwnRestaurantWithOnlyReadRestaurantPermission() {
        UUID ownerId = UUID.randomUUID();
        UserType ownerType = UserType.create(UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.READ_RESTAURANT));
        UserToken ownerToken = new UserToken(ownerId, "Dono", "dono@ex.com", "dono", ownerType);

        var restaurant = Restaurant.create("Bar do Zé", "Rua A", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        var result = useCase.run(ownerToken, restaurant.getId());

        assertThat(result.getId()).isEqualTo(restaurant.getId());
    }

    @Test
    void shouldThrowForbiddenWhenReadingOtherRestaurantWithoutReadAllPermission() {
        UserType ownerType = UserType.create(UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.READ_RESTAURANT));
        UserToken ownerToken = new UserToken(
                UUID.randomUUID(), "Dono", "dono@ex.com", "dono", ownerType);

        var restaurant = Restaurant.create("Bar do Zé", "Rua A", "Brasileira", "08:00-22:00", UUID.randomUUID());

        when(restaurantGateway.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> useCase.run(ownerToken, restaurant.getId()))
                .isInstanceOf(ForbiddenActionException.class);
    }
}
