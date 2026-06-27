package com.group55.gastoflow_ca.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.CreateUserTypeRequest;
import com.group55.gastoflow_ca.core.controllers.UserTypeController;
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usertype")
public class UserTypeRestAdapter {

    private final UserTypeController userTypeController;

    public UserTypeRestAdapter(UserTypeController userTypeController) {
        this.userTypeController = userTypeController;
    }

    @PostMapping
    public ResponseEntity<UserTypeOutputDTO> create(
            @RequestBody @Valid CreateUserTypeRequest request) {
        CreateUserTypeInputDataDTO input = new CreateUserTypeInputDataDTO(
                request.name(),
                request.permissions());

        UserTypeOutputDTO output = userTypeController.createUserType(input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping
    public ResponseEntity<String> getAll() {
        return ResponseEntity.ok("ok");
    }
}
