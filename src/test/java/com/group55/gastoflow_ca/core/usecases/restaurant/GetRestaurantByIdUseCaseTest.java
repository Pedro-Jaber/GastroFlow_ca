package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.util.Optional;
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
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

@ExtendWith(MockitoExtension.class)
class GetRestaurantByIdUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    private GetRestaurantByIdUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = GetRestaurantByIdUseCase.create(restaurantGateway);
    }

    @Test
    void shouldReturnRestaurantWhenFound() {
        var ownerId = UUID.randomUUID();
        var restaurant = Restaurant.create("Bar do Zé", "Rua A", "Brasileira", "08:00-22:00", ownerId);

        when(restaurantGateway.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));

        var result = useCase.run(restaurant.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurant.getId());
        assertThat(result.getName()).isEqualTo("Bar do Zé");
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        var id = UUID.randomUUID();

        when(restaurantGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(id))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var id = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante", "Endereço", "Japonesa", "10:00-22:00", UUID.randomUUID());

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(restaurant));

        useCase.run(id);

        verify(restaurantGateway, times(1)).findById(id);
    }
}
