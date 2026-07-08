package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.math.BigDecimal;
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

import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

@ExtendWith(MockitoExtension.class)
class GetMenuItemByIdUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    private GetMenuItemByIdUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = GetMenuItemByIdUseCase.create(menuItemGateway);
    }

    @Test
    void shouldReturnMenuItemWhenFound() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create(id, "Pizza", "desc", BigDecimal.TEN, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(menuItem));

        var result = useCase.run(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Pizza");
    }

    @Test
    void shouldThrowWhenMenuItemNotFound() {
        var id = UUID.randomUUID();

        when(menuItemGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(id))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var menuItem = MenuItem.create(id, "Item", "desc", BigDecimal.ONE, true, "/a.png", restaurantId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        when(menuItemGateway.findById(id)).thenReturn(Optional.of(menuItem));

        useCase.run(id);

        verify(menuItemGateway, times(1)).findById(id);
    }
}
