package com.group55.gastoflow_ca.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.core.controllers.AuthController;
import com.group55.gastoflow_ca.core.dtos.auth.LoginInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthRestAdapter {

    private final AuthController authController;

    public AuthRestAdapter(AuthController authController) {
        this.authController = authController;
    }

    @PostMapping("/login")
    public ResponseEntity<UserOutputDTO> login(
            @RequestBody @Valid LoginInputDataDTO loginResquest) {

        UserOutputDTO user = authController.login(loginResquest);

        return ResponseEntity.ok(user);
    }

}
