package com.group55.gastoflow_ca.core.auth;

import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;

public final class AuthorizationChecker {

    private AuthorizationChecker() {
    }

    public static void requirePermission(UserToken requestingUser, Permission required) {
        boolean hasPermission = requestingUser.getUserType().getPermissions().contains(required);

        if (!hasPermission) {
            throw new ForbiddenActionException("Missing permission: " + required);
        }
    }

    public static boolean hasPermission(UserToken requestingUser, Permission required) {
        return requestingUser.getUserType().getPermissions().contains(required);
    }
}
