package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.math.BigDecimal;
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

import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
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

    @BeforeEach
    void setup() {
        useCase = CreateMenuItemUseCase.create(menuItemGateway, restaurantGateway);
    }

    @Test
    void shouldCreateMenuItemSuccessfully() {
        var restaurantId = UUID.randomUUID();
        var restaurant = Restaurant.create("Restaurante A", "Rua 1", "Italiana", "08h-22h", UUID.randomUUID());
        var input = new CreateMenuItemInputDataDTO("Pizza", "desc", new BigDecimal("30.00"), true, "/pizza.png",
                restaurantId);

        when(restaurantGateway.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemGateway.saveNewMenuItem(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = useCase.run(input);

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

        assertThatThrownBy(() -> useCase.run(input))
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

        useCase.run(input);

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

        var result = useCase.run(input);

        assertThat(result.getDescription()).isEqualTo("Com carne");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("45.00"));
        assertThat(result.isOnlyInRestaurant()).isTrue();
        assertThat(result.getPhotoPath()).isEqualTo("/lasanha.png");
    }
}
