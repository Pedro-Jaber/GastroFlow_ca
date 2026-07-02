package com.group55.gastoflow_ca.api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email address is required") String emailAddress,
        @NotBlank(message = "Login is required") String login,
        @NotBlank(message = "Password is required") String password,
        @NotNull(message = "User type is required") UUID userTypeId) {

}
