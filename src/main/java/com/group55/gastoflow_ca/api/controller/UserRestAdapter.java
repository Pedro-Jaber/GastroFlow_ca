package com.group55.gastoflow_ca.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.user.CreateUserRequest;
import com.group55.gastoflow_ca.api.dto.request.user.UpdateUserRequest;
import com.group55.gastoflow_ca.core.controllers.UserController;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.user.UpdateUserInputDataDTO;
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

    @GetMapping
    public ResponseEntity<PageOutputDTO<UserOutputDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageInputDTO pageInput = new PageInputDTO(page, size);
        PageOutputDTO<UserOutputDTO> output = userController.getAllUser(pageInput);

        return ResponseEntity.ok(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOutputDTO> getById(
            @PathVariable UUID id) {

        UserOutputDTO output = userController.getUserById(id);

        return ResponseEntity.ok(output);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserOutputDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request) {

        UpdateUserInputDataDTO input = new UpdateUserInputDataDTO(
                request.name(),
                request.emailAddress(),
                request.login(),
                request.password(),
                request.userTypeId());

        UserOutputDTO output = userController.updateUser(id, input);

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userController.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}
