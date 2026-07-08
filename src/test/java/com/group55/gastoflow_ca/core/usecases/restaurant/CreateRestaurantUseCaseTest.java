package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.util.Optional;
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

import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.exceptions.RestaurantAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private IUserGateway userGateway;

    private CreateRestaurantUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = CreateRestaurantUseCase.create(restaurantGateway, userGateway);
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        var ownerId = UUID.randomUUID();
        var owner = User.create("Zé", "ze@email.com", "ze", "123", null);
        var input = new CreateRestaurantInputDataDTO("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);
        var savedRestaurant = Restaurant.create("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findByName("Bar do Zé")).thenReturn(Optional.empty());
        when(userGateway.findById(ownerId)).thenReturn(Optional.of(owner));
        when(restaurantGateway.saveNewRestaurant(any(Restaurant.class))).thenReturn(savedRestaurant);

        var result = useCase.run(input);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bar do Zé");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        verify(restaurantGateway, times(1)).saveNewRestaurant(any(Restaurant.class));
    }

    @Test
    void shouldThrowWhenRestaurantNameAlreadyExists() {
        var ownerId = UUID.randomUUID();
        var input = new CreateRestaurantInputDataDTO("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);
        var existing = Restaurant.create("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findByName("Bar do Zé")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(RestaurantAlreadyExistsException.class)
                .hasMessageContaining("Bar do Zé");

        verify(userGateway, never()).findById(any());
        verify(restaurantGateway, never()).saveNewRestaurant(any());
    }

    @Test
    void shouldThrowWhenOwnerNotFound() {
        var ownerId = UUID.randomUUID();
        var input = new CreateRestaurantInputDataDTO("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findByName("Bar do Zé")).thenReturn(Optional.empty());
        when(userGateway.findById(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(input))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(ownerId.toString());

        verify(restaurantGateway, never()).saveNewRestaurant(any());
    }

    @Test
    void shouldCheckForExistingNameBeforeCheckingOwner() {
        var ownerId = UUID.randomUUID();
        var owner = User.create("Zé", "ze@email.com", "ze", "123", null);
        var input = new CreateRestaurantInputDataDTO("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);
        var saved = Restaurant.create("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findByName("Bar do Zé")).thenReturn(Optional.empty());
        when(userGateway.findById(ownerId)).thenReturn(Optional.of(owner));
        when(restaurantGateway.saveNewRestaurant(any())).thenReturn(saved);

        useCase.run(input);

        var inOrder = org.mockito.Mockito.inOrder(restaurantGateway, userGateway);
        inOrder.verify(restaurantGateway).findByName("Bar do Zé");
        inOrder.verify(userGateway).findById(ownerId);
        inOrder.verify(restaurantGateway).saveNewRestaurant(any());
    }
}
