package com.group55.gastoflow_ca.api.controller;

import java.math.BigDecimal;
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

import com.group55.gastoflow_ca.api.dto.request.menuItem.CreateMenuItemRequest;
import com.group55.gastoflow_ca.api.dto.request.menuItem.UpdateMenuItemRequest;
import com.group55.gastoflow_ca.core.controllers.MenuItemController;
import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.UpdateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;

@ExtendWith(MockitoExtension.class)
class MenuItemRestAdapterTest {

    @Mock
    private MenuItemController menuItemController;

    private MenuItemRestAdapter adapter;

    private UserToken userToken;

    @BeforeEach
    void setup() {
        adapter = new MenuItemRestAdapter(menuItemController);

        UserType userType = UserType.create(UUID.randomUUID(), "RestaurantOwner", Set.of());
        userToken = new UserToken(UUID.randomUUID(), "Owner", "owner@ex.com", "owner", userType);
    }

    @Test
    void shouldCreateMenuItemAndReturn201() {
        UUID restaurantId = UUID.randomUUID();
        var request = new CreateMenuItemRequest("Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId);
        var output = new MenuItemOutputDTO(UUID.randomUUID(), "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, restaurantId, LocalDateTime.now(), LocalDateTime.now());

        when(menuItemController.createMenuItem(eq(userToken), any(CreateMenuItemInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<MenuItemOutputDTO> response = adapter.create(userToken, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(output);
        verify(menuItemController).createMenuItem(userToken,
                new CreateMenuItemInputDataDTO("Feijoada Completa", "Feijoada tradicional",
                        new BigDecimal("39.90"), false, null, restaurantId));
    }

    @Test
    void shouldReturnPageOfMenuItems() {
        var output = new MenuItemOutputDTO(UUID.randomUUID(), "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());
        var page = new PageOutputDTO<>(List.of(output), 0, 10, 1L, 1);

        when(menuItemController.getAllMenuItems(eq(userToken), any(PageInputDTO.class))).thenReturn(page);

        ResponseEntity<PageOutputDTO<MenuItemOutputDTO>> response = adapter.getAll(userToken, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
    }

    @Test
    void shouldReturnMenuItemById() {
        UUID id = UUID.randomUUID();
        var output = new MenuItemOutputDTO(id, "Feijoada Completa", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(menuItemController.getMenuItemById(userToken, id)).thenReturn(output);

        ResponseEntity<MenuItemOutputDTO> response = adapter.getById(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
    }

    @Test
    void shouldUpdateMenuItem() {
        UUID id = UUID.randomUUID();
        var request = new UpdateMenuItemRequest("Feijoada Premium", null, null, null, null, null);
        var output = new MenuItemOutputDTO(id, "Feijoada Premium", "Feijoada tradicional",
                new BigDecimal("39.90"), false, null, UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(menuItemController.updateMenuItem(eq(userToken), eq(id), any(UpdateMenuItemInputDataDTO.class)))
                .thenReturn(output);

        ResponseEntity<MenuItemOutputDTO> response = adapter.update(userToken, id, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(output);
        verify(menuItemController).updateMenuItem(userToken, id,
                new UpdateMenuItemInputDataDTO("Feijoada Premium", null, null, null, null, null));
    }

    @Test
    void shouldDeleteMenuItemAndReturn204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = adapter.delete(userToken, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(menuItemController, times(1)).deleteMenuItem(userToken, id);
    }
}
