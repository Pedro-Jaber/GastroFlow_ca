package com.group55.gastoflow_ca.core.entities;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class MenuItemTest {

    @Test
    void shouldCreateMenuItemWithGeneratedId() {
        var restaurantId = UUID.randomUUID();

        var menuItem = MenuItem.create(
                "Pizza Margherita",
                "Molho de tomate, mussarela e manjericão",
                new BigDecimal("39.90"),
                true,
                "/photos/pizza.png",
                restaurantId);

        assertThat(menuItem.getId()).isNotNull();
        assertThat(menuItem.getName()).isEqualTo("Pizza Margherita");
        assertThat(menuItem.getDescription()).isEqualTo("Molho de tomate, mussarela e manjericão");
        assertThat(menuItem.getPrice()).isEqualTo(new BigDecimal("39.90"));
        assertThat(menuItem.isOnlyInRestaurant()).isTrue();
        assertThat(menuItem.getPhotoPath()).isEqualTo("/photos/pizza.png");
        assertThat(menuItem.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(menuItem.getCreatedAt()).isNotNull();
        assertThat(menuItem.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldCreateMenuItemWithProvidedId() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var createdAt = java.time.LocalDateTime.now().minusDays(1);
        var updatedAt = java.time.LocalDateTime.now();

        var menuItem = MenuItem.create(
                id,
                "Suco de Laranja",
                "Suco natural",
                new BigDecimal("8.50"),
                false,
                "/photos/suco.png",
                restaurantId,
                createdAt,
                updatedAt);

        assertThat(menuItem.getId()).isEqualTo(id);
        assertThat(menuItem.getName()).isEqualTo("Suco de Laranja");
        assertThat(menuItem.isOnlyInRestaurant()).isFalse();
        assertThat(menuItem.getCreatedAt()).isEqualTo(createdAt);
        assertThat(menuItem.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldGenerateDifferentIdsForEachInstance() {
        var restaurantId = UUID.randomUUID();

        var item1 = MenuItem.create("Item A", "desc", BigDecimal.TEN, true, "/a.png", restaurantId);
        var item2 = MenuItem.create("Item B", "desc", BigDecimal.TEN, true, "/b.png", restaurantId);

        assertThat(item1.getId()).isNotEqualTo(item2.getId());
    }

    @Test
    void shouldConsiderTwoMenuItemsEqualWhenAllFieldsMatch() {
        var id = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        var createdAt = java.time.LocalDateTime.now();
        var updatedAt = java.time.LocalDateTime.now();

        var item1 = MenuItem.create(id, "Item", "desc", BigDecimal.TEN, true, "/a.png", restaurantId, createdAt,
                updatedAt);
        var item2 = MenuItem.create(id, "Item", "desc", BigDecimal.TEN, true, "/a.png", restaurantId, createdAt,
                updatedAt);

        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void shouldAllowUpdatingFieldsThroughSetters() {
        var menuItem = MenuItem.create("Item", "desc", BigDecimal.ONE, true, "/a.png", UUID.randomUUID());

        menuItem.setName("Novo Nome");
        menuItem.setPrice(new BigDecimal("15.00"));
        menuItem.setOnlyInRestaurant(false);

        assertThat(menuItem.getName()).isEqualTo("Novo Nome");
        assertThat(menuItem.getPrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(menuItem.isOnlyInRestaurant()).isFalse();
    }
}
