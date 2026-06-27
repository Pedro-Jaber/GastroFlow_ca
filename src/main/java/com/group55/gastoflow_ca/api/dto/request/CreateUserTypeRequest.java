package com.group55.gastoflow_ca.api.dto.request;

import java.util.Set;

import com.group55.gastoflow_ca.core.enums.Permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserTypeRequest(
                @NotBlank(message = "Name is required") String name,
                @NotNull(message = "Permissions are required") Set<Permission> permissions) {

}
