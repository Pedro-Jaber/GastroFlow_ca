package com.group55.gastoflow_ca.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.response.LoginResponse;
import com.group55.gastoflow_ca.core.controllers.AuthController;
import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.interfaces.auth.ITokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthRestAdapter {

    private final AuthController authController;
    private final ITokenProvider tokenProvider;

    public AuthRestAdapter(AuthController authController, ITokenProvider tokenProvider) {
        this.authController = authController;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginInputDataDTO loginResquest) {

        UserToken userToken = authController.login(loginResquest);

        String token = tokenProvider.generateToken(userToken);

        return ResponseEntity.ok(new LoginResponse(token));
    }

}
