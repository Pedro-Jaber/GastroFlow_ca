package com.group55.gastoflow_ca.core.dtos.usertype;

import java.util.Set;
import java.util.UUID;

import com.group55.gastoflow_ca.core.enums.Permission;

public record UserTypeDTO(
        UUID id,
        String name,
        Set<Permission> permissions) {

}
