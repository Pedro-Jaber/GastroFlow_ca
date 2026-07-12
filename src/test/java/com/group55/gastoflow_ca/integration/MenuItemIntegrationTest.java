package com.group55.gastoflow_ca.integration;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.group55.gastoflow_ca.api.dto.request.menuItem.CreateMenuItemRequest;
import com.group55.gastoflow_ca.api.dto.request.menuItem.UpdateMenuItemRequest;

class MenuItemIntegrationTest extends IntegrationTestBase {

    @Test
    void shouldCreateGetUpdateAndDeleteMenuItemAsAdmin() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        UUID restaurantId = findRestaurantIdByName(adminToken, SEED_RESTAURANT_NAME);

        var createRequest = new CreateMenuItemRequest(
                "Brigadeiro", "Brigadeiro caseiro de colher", new BigDecimal("6.50"),
                true, "path/to/brigadeiro.jpg", restaurantId);

        MvcResult createResult = mockMvc.perform(post("/menu-items")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Brigadeiro"))
                .andExpect(jsonPath("$.restaurantId").value(restaurantId.toString()))
                .andReturn();

        UUID createdId = UUID.fromString(
                objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/menu-items/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Brigadeiro"));

        mockMvc.perform(get("/menu-items")
                .header("Authorization", authHeader(adminToken))
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        var updateRequest = new UpdateMenuItemRequest(
                "Brigadeiro Gourmet", null, new BigDecimal("8.90"), null, null, null);

        mockMvc.perform(put("/menu-items/" + createdId)
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Brigadeiro Gourmet"))
                .andExpect(jsonPath("$.price").value(8.90));

        mockMvc.perform(delete("/menu-items/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/menu-items/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowRestaurantOwnerToCreateMenuItemForOwnRestaurant() throws Exception {
        String ownerToken = loginAndGetToken(RESTAURANT_OWNER_LOGIN, RESTAURANT_OWNER_PASSWORD);
        UUID restaurantId = findRestaurantIdByName(ownerToken, SEED_RESTAURANT_NAME);

        var createRequest = new CreateMenuItemRequest(
                "Refrigerante", "Lata 350ml", new BigDecimal("5.00"),
                false, "path/to/refrigerante.jpg", restaurantId);

        mockMvc.perform(post("/menu-items")
                .header("Authorization", authHeader(ownerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnForbiddenWhenClienteTriesToCreateMenuItem() throws Exception {
        String clienteToken = loginAndGetToken(CLIENTE_LOGIN, CLIENTE_PASSWORD);
        UUID restaurantId = findRestaurantIdByName(clienteToken, SEED_RESTAURANT_NAME);

        var createRequest = new CreateMenuItemRequest(
                "Agua com Gas", "Garrafa 500ml", new BigDecimal("4.00"),
                false, "path/to/agua.jpg", restaurantId);

        mockMvc.perform(post("/menu-items")
                .header("Authorization", authHeader(clienteToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingMenuItemForUnknownRestaurant() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);

        var createRequest = new CreateMenuItemRequest(
                "Item Fantasma", "Nao existe", new BigDecimal("1.00"),
                false, "path/to/ghost.jpg", UUID.randomUUID());

        mockMvc.perform(post("/menu-items")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }
}
