package com.group55.gastoflow_ca.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.CreateUserRequest;
import com.group55.gastoflow_ca.core.controllers.UserController;
import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserRestAdapter {

    private final UserController userController;

    public UserRestAdapter(UserController userController) {
        this.userController = userController;
    }

    @PostMapping
    public ResponseEntity<UserOutputDTO> create(
            @RequestBody @Valid CreateUserRequest request) {
        CreateUserInputDataDTO input = new CreateUserInputDataDTO(
                request.name(),
                request.emailAddress(),
                request.login(),
                request.password(),
                request.userTypeId());

        UserOutputDTO output = userController.createUser(input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

}
