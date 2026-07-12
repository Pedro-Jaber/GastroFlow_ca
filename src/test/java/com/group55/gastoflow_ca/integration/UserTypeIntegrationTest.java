package com.group55.gastoflow_ca.integration;

import java.util.Set;
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

import com.group55.gastoflow_ca.api.dto.request.CreateUserTypeRequest;
import com.group55.gastoflow_ca.api.dto.request.UpdateUserTypeResquest;
import com.group55.gastoflow_ca.core.enums.Permission;

class UserTypeIntegrationTest extends IntegrationTestBase {

    @Test
    void shouldCreateGetUpdateAndDeleteUserTypeAsAdmin() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);

        var createRequest = new CreateUserTypeRequest("Entregador", Set.of(Permission.READ_RESTAURANT));

        MvcResult createResult = mockMvc.perform(post("/usertype")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Entregador"))
                .andReturn();

        UUID createdId = UUID.fromString(
                objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/usertype/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Entregador"));

        mockMvc.perform(get("/usertype")
                .header("Authorization", authHeader(adminToken))
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        var updateRequest = new UpdateUserTypeResquest("Entregador Premium", Set.of(Permission.READ_ALL_RESTAURANT));

        mockMvc.perform(put("/usertype/" + createdId)
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Entregador Premium"));

        mockMvc.perform(delete("/usertype/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/usertype/" + createdId)
                .header("Authorization", authHeader(adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenClienteTriesToCreateUserType() throws Exception {
        String clienteToken = loginAndGetToken(CLIENTE_LOGIN, CLIENTE_PASSWORD);

        var createRequest = new CreateUserTypeRequest("Gerente", Set.of());

        mockMvc.perform(post("/usertype")
                .header("Authorization", authHeader(clienteToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnConflictWhenCreatingUserTypeWithDuplicateName() throws Exception {
        String adminToken = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);

        var createRequest = new CreateUserTypeRequest("Cliente", Set.of());

        mockMvc.perform(post("/usertype")
                .header("Authorization", authHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict());
    }
}
