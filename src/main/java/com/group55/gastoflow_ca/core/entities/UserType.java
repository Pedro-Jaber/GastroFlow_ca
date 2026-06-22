package com.group55.gastoflow_ca.core.entities;

import java.util.Set;
import java.util.UUID;

import com.group55.gastoflow_ca.core.enums.Permission;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class UserType {
    private UUID id;
    private String name;
    private Set<Permission> permissions;

    public static UserType create(
            UUID id,
            String name,
            Set<Permission> permissions) {

        UserType userType = new UserType();

        userType.id = id;
        userType.name = name;
        userType.permissions = permissions;

        return userType;
    }
}
