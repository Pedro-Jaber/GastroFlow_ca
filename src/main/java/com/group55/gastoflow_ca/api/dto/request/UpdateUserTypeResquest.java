package com.group55.gastoflow_ca.api.dto.request;

import java.util.Set;

import com.group55.gastoflow_ca.core.enums.Permission;

public record UpdateUserTypeResquest(
        String name,
        Set<Permission> permissions) {

}
