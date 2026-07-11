package com.group55.gastoflow_ca.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.CreateUserTypeRequest;
import com.group55.gastoflow_ca.api.dto.request.UpdateUserTypeResquest;
import com.group55.gastoflow_ca.core.controllers.UserTypeController;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.CreateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UpdateUserTypeInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;

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
            @AuthenticationPrincipal UserToken userToken,
            @RequestBody @Valid CreateUserTypeRequest request) {
        CreateUserTypeInputDataDTO input = new CreateUserTypeInputDataDTO(
                request.name(),
                request.permissions());

        UserTypeOutputDTO output = userTypeController.createUserType(userToken, input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping
    public ResponseEntity<PageOutputDTO<UserTypeOutputDTO>> getAll(
            @AuthenticationPrincipal UserToken userToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageInputDTO pageInput = new PageInputDTO(page, size);
        PageOutputDTO<UserTypeOutputDTO> output = userTypeController.getAllUserType(userToken, pageInput);

        return ResponseEntity.ok(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeOutputDTO> getById(
            @AuthenticationPrincipal UserToken userToken,
            @PathVariable UUID id) {
        UserTypeOutputDTO output = userTypeController.getUserTypeById(userToken, id);

        return ResponseEntity.ok(output);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserTypeOutputDTO> update(
            @AuthenticationPrincipal UserToken userToken,
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserTypeResquest resquest) {

        UpdateUserTypeInputDataDTO input = new UpdateUserTypeInputDataDTO(
                resquest.name(),
                resquest.permissions());

        UserTypeOutputDTO output = userTypeController.updateUserType(userToken, id, input);

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserToken userToken,
            @PathVariable UUID id) {
        userTypeController.deleteUserType(userToken, id);

        return ResponseEntity.noContent().build();
    }

}
