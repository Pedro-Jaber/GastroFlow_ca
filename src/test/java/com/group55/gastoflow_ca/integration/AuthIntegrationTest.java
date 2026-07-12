package com.group55.gastoflow_ca.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;

class AuthIntegrationTest extends IntegrationTestBase {

    @Test
    void shouldLoginWithSeededAdminAndReturnJwtToken() throws Exception {
        String token = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);

        assertThat(token).isNotBlank();
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new LoginInputDataDTO(ADMIN_LOGIN, "wrong-password"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForUnknownLogin() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new LoginInputDataDTO("unknown-user", "password123"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMalformed() throws Exception {
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer this-is-not-a-valid-jwt")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessToProtectedEndpointWithValidToken() throws Exception {
        String token = loginAndGetToken(ADMIN_LOGIN, ADMIN_PASSWORD);

        mockMvc.perform(get("/users")
                .header("Authorization", authHeader(token))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
