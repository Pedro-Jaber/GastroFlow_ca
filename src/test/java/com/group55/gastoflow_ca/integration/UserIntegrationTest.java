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

import com.group55.gastoflow_ca.api.dto.request.user.CreateUserRequest;
import com.group55.gastoflow_ca.api.dto.request.user.UpdateUserRequest;

class UserIntegrationTest extends IntegrationTestBase {

    @Test
    void shouldCreateGetUpdateAndDeleteUserAsAdmin() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        UUID clienteUserTypeId = findUserTypeIdByName(adminToken, "Cliente");

        var createRequest = new CreateUserRequest(
                "Maria Silva", "maria@ex.com", "maria", "password123", clienteUserTypeId);

        MvcResult createResult = mockMvc.perform(post("/users")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("maria"))
                .andReturn();

        UUID createdId = UUID.fromString(
                objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/users/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("maria"));

        var updateRequest = new UpdateUserRequest("Maria Silva Santos", null, null, null, null);

        mockMvc.perform(put("/users/" + createdId)
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Silva Santos"));

        mockMvc.perform(delete("/users/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenLoginAlreadyExists() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        UUID clienteUserTypeId = findUserTypeIdByName(adminToken, "Cliente");

        var createRequest = new CreateUserRequest(
                "Duplicado", "duplicado@ex.com", CLIENTE_LOGIN, "password123", clienteUserTypeId);

        mockMvc.perform(post("/users")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAllowUserToReadOwnData() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        String clienteToken = loginAndGetToken(CLIENTE_LOGIN, CLIENTE_PASSWORD);
        UUID ownId = findUserIdByLogin(adminToken, CLIENTE_LOGIN);

        mockMvc.perform(get("/users/" + ownId)
                .header("Authorization", authHeader(clienteToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(CLIENTE_LOGIN));
    }

    @Test
    void shouldForbidUserFromReadingAnotherUsersData() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        String clienteToken = loginAndGetToken(CLIENTE_LOGIN, CLIENTE_PASSWORD);
        UUID adminId = findUserIdByLogin(adminToken, ADMIN_LOGIN);

        mockMvc.perform(get("/users/" + adminId)
                .header("Authorization", authHeader(clienteToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenClienteTriesToCreateUser() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);
        String clienteToken = loginAndGetToken(CLIENTE_LOGIN, CLIENTE_PASSWORD);
        UUID clienteUserTypeId = findUserTypeIdByName(adminToken, "Cliente");

        var createRequest = new CreateUserRequest(
                "Novo Usuario", "novo@ex.com", "novo", "password123", clienteUserTypeId);

        mockMvc.perform(post("/users")
                .header("Authorization", authHeader(clienteToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}
