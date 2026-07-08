package com.group55.gastoflow_ca.core.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class RestaurantTest {

    @Test
    void shouldCreateRestaurantWithGeneratedId() {
        var ownerId = UUID.randomUUID();

        var restaurant = Restaurant.create("Bar do Zé", "Rua A, 123", "Brasileira", "08:00-22:00", ownerId);

        assertThat(restaurant.getId()).isNotNull();
        assertThat(restaurant.getName()).isEqualTo("Bar do Zé");
        assertThat(restaurant.getAddress()).isEqualTo("Rua A, 123");
        assertThat(restaurant.getCuisineType()).isEqualTo("Brasileira");
        assertThat(restaurant.getOpeningHours()).isEqualTo("08:00-22:00");
        assertThat(restaurant.getOwnerId()).isEqualTo(ownerId);
        assertThat(restaurant.getCreatedAt()).isNotNull();
        assertThat(restaurant.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldCreateRestaurantWithProvidedId() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var createdAt = LocalDateTime.now().minusDays(1);
        var updatedAt = LocalDateTime.now();

        var restaurant = Restaurant.create(id, "Cantina Italiana", "Rua B, 456", "Italiana", "12:00-23:00",
                ownerId, createdAt, updatedAt);

        assertThat(restaurant.getId()).isEqualTo(id);
        assertThat(restaurant.getCreatedAt()).isEqualTo(createdAt);
        assertThat(restaurant.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void shouldGenerateDifferentIdsForEachInstance() {
        var ownerId = UUID.randomUUID();

        var restaurant1 = Restaurant.create("Restaurante A", "Endereço A", "Japonesa", "10:00-22:00", ownerId);
        var restaurant2 = Restaurant.create("Restaurante B", "Endereço B", "Mexicana", "10:00-22:00", ownerId);

        assertThat(restaurant1.getId()).isNotEqualTo(restaurant2.getId());
    }

    @Test
    void shouldConsiderTwoRestaurantsEqualWhenAllFieldsMatch() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var createdAt = LocalDateTime.now();
        var updatedAt = LocalDateTime.now();

        var restaurant1 = Restaurant.create(id, "Restaurante", "Endereço", "Brasileira", "08:00-22:00", ownerId,
                createdAt, updatedAt);
        var restaurant2 = Restaurant.create(id, "Restaurante", "Endereço", "Brasileira", "08:00-22:00", ownerId,
                createdAt, updatedAt);

        assertThat(restaurant1).isEqualTo(restaurant2);
    }

    @Test
    void shouldAllowUpdatingFieldsViaSetters() {
        var ownerId = UUID.randomUUID();
        var restaurant = Restaurant.create("Nome Antigo", "Endereço Antigo", "Brasileira", "08:00-18:00", ownerId);

        var newOwnerId = UUID.randomUUID();
        restaurant.setName("Nome Novo");
        restaurant.setAddress("Endereço Novo");
        restaurant.setCuisineType("Francesa");
        restaurant.setOpeningHours("09:00-23:00");
        restaurant.setOwnerId(newOwnerId);

        assertThat(restaurant.getName()).isEqualTo("Nome Novo");
        assertThat(restaurant.getAddress()).isEqualTo("Endereço Novo");
        assertThat(restaurant.getCuisineType()).isEqualTo("Francesa");
        assertThat(restaurant.getOpeningHours()).isEqualTo("09:00-23:00");
        assertThat(restaurant.getOwnerId()).isEqualTo(newOwnerId);
    }
}
