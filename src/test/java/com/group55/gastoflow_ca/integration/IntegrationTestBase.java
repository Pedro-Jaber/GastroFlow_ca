package com.group55.gastoflow_ca.integration;

import java.util.UUID;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.group55.gastoflow_ca.api.dto.response.LoginResponse;
import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Base class for full Spring Boot integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
public abstract class IntegrationTestBase {

    protected static final String ADMIN_LOGIN = "admin";
    protected static final String ADMIN_PASSWORD = "admin123";

    protected static final String CLIENTE_LOGIN = "cliente";
    protected static final String CLIENTE_PASSWORD = "senha123";

    protected static final String RESTAURANT_OWNER_LOGIN = "restowner";
    protected static final String RESTAURANT_OWNER_PASSWORD = "senha123";

    protected static final String SEED_RESTAURANT_NAME = "Sabor Caseiro";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String authHeader(String token) {
        return "Bearer " + token;
    }

    protected String loginAndGetToken(String login, String password) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new LoginInputDataDTO(login, password));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);

        return response.token();
    }

    protected UUID findUserIdByLogin(String token, String login) throws Exception {
        MvcResult result = mockMvc.perform(get("/users")
                .header("Authorization", authHeader(token))
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode content = objectMapper.readTree(result.getResponse().getContentAsString()).get("content");

        for (JsonNode userNode : content) {
            if (userNode.get("login").asText().equals(login)) {
                return UUID.fromString(userNode.get("id").asText());
            }
        }

        throw new IllegalStateException("Seeded user not found: " + login);
    }

    protected UUID findUserTypeIdByName(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(get("/usertype")
                .header("Authorization", authHeader(token))
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode content = objectMapper.readTree(result.getResponse().getContentAsString()).get("content");

        for (JsonNode userTypeNode : content) {
            if (userTypeNode.get("name").asText().equals(name)) {
                return UUID.fromString(userTypeNode.get("id").asText());
            }
        }

        throw new IllegalStateException("Seeded user type not found: " + name);
    }

    protected UUID findRestaurantIdByName(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(get("/restaurants")
                .header("Authorization", authHeader(token))
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode content = objectMapper.readTree(result.getResponse().getContentAsString()).get("content");

        for (JsonNode restaurantNode : content) {
            if (restaurantNode.get("name").asText().equals(name)) {
                return UUID.fromString(restaurantNode.get("id").asText());
            }
        }

        throw new IllegalStateException("Seeded restaurant not found: " + name);
    }
}
