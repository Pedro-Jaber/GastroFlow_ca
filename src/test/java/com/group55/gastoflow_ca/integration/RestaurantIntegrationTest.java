package com.group55.gastoflow_ca.integration;

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

import com.group55.gastoflow_ca.api.dto.request.restaurant.CreateRestaurantRequest;
import com.group55.gastoflow_ca.api.dto.request.restaurant.UpdateRestaurantRequest;

class RestaurantIntegrationTest extends IntegrationTestBase {

    @Test
    void shouldCreateGetUpdateAndDeleteRestaurantAsAdmin() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        UUID ownerId = findUserIdByLogin(adminToken, RESTAURANT_OWNER_LOGIN);

        var createRequest = new CreateRestaurantRequest(
                "Cantina da Serra", "Rua das Palmeiras, 45", "Italiana", "11:00 - 23:00", ownerId);

        MvcResult createResult = mockMvc.perform(post("/restaurants")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cantina da Serra"))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()))
                .andReturn();

        UUID createdId = UUID.fromString(
                objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/restaurants/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cantina da Serra"));

        mockMvc.perform(get("/restaurants")
                .header("Authorization", authHeader(adminToken))
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        var updateRequest = new UpdateRestaurantRequest("Cantina da Serra - Centro", null, null, null, null);

        mockMvc.perform(put("/restaurants/" + createdId)
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cantina da Serra - Centro"));

        mockMvc.perform(delete("/restaurants/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurants/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowRestaurantOwnerToCreateOwnRestaurant() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        String ownerToken = loginAndGetToken(RESTAURANT_OWNER_LOGIN, RESTAURANT_OWNER_PASSWORD);
        UUID ownerId = findUserIdByLogin(adminToken, RESTAURANT_OWNER_LOGIN);

        var createRequest = new CreateRestaurantRequest(
                "Pizzaria do Bairro", "Av. Central, 900", "Pizzaria", "18:00 - 23:59", ownerId);

        mockMvc.perform(post("/restaurants")
                .header("Authorization", authHeader(ownerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));
    }

    @Test
    void shouldReturnForbiddenWhenClienteTriesToCreateRestaurant() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        String clienteToken = loginAndGetToken(CLIENTE_LOGIN, CLIENTE_PASSWORD);
        UUID ownerId = findUserIdByLogin(adminToken, CLIENTE_LOGIN);

        var createRequest = new CreateRestaurantRequest(
                "Restaurante do Cliente", "Rua X, 1", "Brasileira", "08:00 - 20:00", ownerId);

        mockMvc.perform(post("/restaurants")
                .header("Authorization", authHeader(clienteToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenGettingUnknownRestaurant() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);

        mockMvc.perform(get("/restaurants/" + UUID.randomUUID())
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNotFound());
    }
}
