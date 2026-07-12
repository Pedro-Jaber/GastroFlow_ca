package com.group55.gastoflow_ca.core.auth;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;

class AuthorizationCheckerTest {

    private UserToken userTokenWithPermissions(Set<Permission> permissions) {
        UserType userType = UserType.create(UUID.randomUUID(), "Some Type", permissions);
        return new UserToken(UUID.randomUUID(), "Some User", "user@ex.com", "user", userType);
    }

    @Test
    void hasPermissionShouldReturnTrueWhenUserHasPermission() {
        UserToken userToken = userTokenWithPermissions(Set.of(Permission.CREATE_USER));

        boolean result = AuthorizationChecker.hasPermission(userToken, Permission.CREATE_USER);

        assertThat(result).isTrue();
    }

    @Test
    void hasPermissionShouldReturnFalseWhenUserLacksPermission() {
        UserToken userToken = userTokenWithPermissions(Set.of(Permission.READ_USER));

        boolean result = AuthorizationChecker.hasPermission(userToken, Permission.CREATE_USER);

        assertThat(result).isFalse();
    }

    @Test
    void hasPermissionShouldReturnFalseWhenUserHasNoPermissions() {
        UserToken userToken = userTokenWithPermissions(Set.of());

        boolean result = AuthorizationChecker.hasPermission(userToken, Permission.CREATE_USER);

        assertThat(result).isFalse();
    }

    @Test
    void requirePermissionShouldNotThrowWhenUserHasPermission() {
        UserToken userToken = userTokenWithPermissions(Set.of(Permission.DELETE_RESTAURANT));

        assertThatCodeDoesNotThrow(() -> AuthorizationChecker.requirePermission(userToken,
                Permission.DELETE_RESTAURANT));
    }

    @Test
    void requirePermissionShouldThrowForbiddenActionExceptionWhenUserLacksPermission() {
        UserToken userToken = userTokenWithPermissions(Set.of(Permission.READ_RESTAURANT));

        assertThatThrownBy(() -> AuthorizationChecker.requirePermission(userToken, Permission.DELETE_RESTAURANT))
                .isInstanceOf(ForbiddenActionException.class)
                .hasMessageContaining("DELETE_RESTAURANT");
    }

    @Test
    void requirePermissionShouldThrowForbiddenActionExceptionWhenUserHasNoPermissions() {
        UserToken userToken = userTokenWithPermissions(Set.of());

        assertThatThrownBy(() -> AuthorizationChecker.requirePermission(userToken, Permission.CREATE_MENU_ITEM))
                .isInstanceOf(ForbiddenActionException.class);
    }

    private void assertThatCodeDoesNotThrow(Runnable runnable) {
        runnable.run();
    }
}
