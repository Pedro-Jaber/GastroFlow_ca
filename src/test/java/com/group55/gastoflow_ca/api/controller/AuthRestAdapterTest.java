package com.group55.gastoflow_ca.api.controller;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.group55.gastoflow_ca.api.dto.response.LoginResponse;
import com.group55.gastoflow_ca.core.controllers.AuthController;
import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.interfaces.auth.ITokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthRestAdapterTest {

    @Mock
    private AuthController authController;

    @Mock
    private ITokenProvider tokenProvider;

    private AuthRestAdapter adapter;

    @BeforeEach
    void setup() {
        adapter = new AuthRestAdapter(authController, tokenProvider);
    }

    @Test
    void shouldLoginAndReturnGeneratedToken() {
        var userType = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        var userToken = new UserToken(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", userType);
        var loginRequest = new LoginInputDataDTO("jdoe", "password123");

        when(authController.login(loginRequest)).thenReturn(userToken);
        when(tokenProvider.generateToken(userToken)).thenReturn("jwt-token-value");

        ResponseEntity<LoginResponse> response = adapter.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo("jwt-token-value");
    }
}
