package com.group55.gastoflow_ca.core.entities;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.group55.gastoflow_ca.core.enums.Permission;

class UserTypeTest {

    @Test
    void shouldCreateUserTypeWithGeneratedId() {

        String name = "Dono de Restaurante";
        Set<Permission> permissions = Set.of(Permission.CREATE_RESTAURANT);

        var userType = UserType.create(name, permissions);

        assertThat(userType.getId()).isNotNull();
        assertThat(userType.getName()).isEqualTo(name);
        assertThat(userType.getPermissions()).containsExactly(Permission.CREATE_RESTAURANT);
    }

    @Test
    void shouldCreateUserTypeWithProvidedId() {
        var id = UUID.randomUUID();
        String name = "Cliente";
        Set<Permission> permissions = Set.of(Permission.CREATE_RESTAURANT);

        var userType = UserType.create(id, name, permissions);

        assertThat(userType.getId()).isEqualTo(id);
        assertThat(userType.getName()).isEqualTo(name);
        assertThat(userType.getPermissions()).containsExactly(Permission.CREATE_RESTAURANT);
    }

    @Test
    void shouldGenerateDifferentIdsForEachInstance() {
        var userType1 = UserType.create("Tipo A", Set.of());
        var userType2 = UserType.create("Tipo B", Set.of());

        assertThat(userType1.getId()).isNotEqualTo(userType2.getId());
    }

    @Test
    void shouldConsiderTwoUserTypesEqualWhenAllFieldsMatch() {
        var id = UUID.randomUUID();
        String name = "Dono";
        Set<Permission> permissions = Set.of(Permission.CREATE_RESTAURANT);

        var userType1 = UserType.create(id, name, permissions);
        var userType2 = UserType.create(id, name, permissions);

        assertThat(userType1).isEqualTo(userType2);
    }

    @Test
    void shouldCreateUserTypeWithEmptyPermissions() {
        var userType = UserType.create("Visitante", Set.of());

        assertThat(userType.getPermissions()).isEmpty();
    }

    @Test
    void shouldCreateUserTypeWithMultiplePermissions() {
        Set<Permission> permissions = Set.of(
                Permission.CREATE_RESTAURANT,
                Permission.EDIT_RESTAURANT,
                Permission.CREATE_MENU_ITEM);

        var userType = UserType.create("Gerente", permissions);

        assertThat(userType.getPermissions()).hasSize(3);
        assertThat(userType.getPermissions()).containsAll(permissions);
    }
}