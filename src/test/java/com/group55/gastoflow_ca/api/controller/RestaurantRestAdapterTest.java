package com.group55.gastoflow_ca.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.group55.gastoflow_ca.api.dto.request.restaurant.CreateRestaurantRequest;
import com.group55.gastoflow_ca.api.dto.request.restaurant.UpdateRestaurantRequest;
import com.group55.gastoflow_ca.core.controllers.RestaurantController;
import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantOutputDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.UpdateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;

@ExtendWith(MockitoExtension.class)
class RestaurantRestAdapterTest {

    @Mock
    private RestaurantController restaurantController;

    private RestaurantRestAdapter adapter;

    private UserToken userToken;

    @BeforeEach
    void setup() {
        adapter = new RestaurantRestAdapter(restaurantController);

        UserType userType = UserType.create(UUID.randomUUID(), "RestaurantOwner", Set.of());
        userToken = new UserToken(UUID.randomUUID(), "Owner", "owner@ex.com", "owner", userType);
    }

    @Test
    void shouldCreateRestaurantAndReturn201() {
        UUID ownerId = UUID.randomUUID();
        var request = new CreateRestaurantRequest("Sabor Caseiro", "Rua das Flores, 123", "Brasileira",
                "08:00 - 22:00", ownerId);
        var output = new RestaurantOutputDTO(UUID.randomUUID(), "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", ownerId, LocalDateTime.now(), LocalDateTime.now());

        when(restaurantController.createRestaurant(eq(userToken), any(CreateRestaurantInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<RestaurantOutputDTO> response = adapter.create(userToken, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
        verify(restaurantController).createRestaurant(userToken,
                new CreateRestaurantInputDataDTO("Sabor Caseiro", "Rua das Flores, 123", "Brasileira",
                        "08:00 - 22:00", ownerId));
    }

    @Test
    void shouldReturnPageOfRestaurants() {
        var output = new RestaurantOutputDTO(UUID.randomUUID(), "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        var page = new PageOutputDTO<>(List.of(output), 0, 10, 1L, 1);

        when(restaurantController.getAllRestaurants(eq(userToken), any(PageInputDTO.class))).thenReturn(page);

        ResponseEntity<PageOutputDTO<RestaurantOutputDTO>> response = adapter.getAll(userToken, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
    }

    @Test
    void shouldReturnRestaurantById() {
        UUID id = UUID.randomUUID();
        var output = new RestaurantOutputDTO(id, "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(restaurantController.getRestaurantById(userToken, id)).thenReturn(output);

        ResponseEntity<RestaurantOutputDTO> response = adapter.getById(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void shouldUpdateRestaurant() {
        UUID id = UUID.randomUUID();
        var request = new UpdateRestaurantRequest("Novo Nome", null, null, null, null);
        var output = new RestaurantOutputDTO(id, "Novo Nome", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(restaurantController.updateRestaurant(eq(userToken), eq(id), any(UpdateRestaurantInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<RestaurantOutputDTO> response = adapter.update(userToken, id, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
        verify(restaurantController).updateRestaurant(userToken, id,
                new UpdateRestaurantInputDataDTO("Novo Nome", null, null, null, null));
    }

    @Test
    void shouldDeleteRestaurantAndReturn204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = adapter.delete(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(restaurantController, times(1)).deleteRestaurant(userToken, id);
    }
}
