package com.group55.gastoflow_ca.core.dtos.usertype;

import java.util.Set;

import com.group55.gastoflow_ca.core.enums.Permission;

public record UpdateUserTypeInputDataDTO(
        String name,
        Set<Permission> permissions) {

}
