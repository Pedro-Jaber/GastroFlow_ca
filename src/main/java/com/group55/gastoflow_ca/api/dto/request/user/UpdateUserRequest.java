package com.group55.gastoflow_ca.api.dto.request.user;

import java.util.UUID;

public record UpdateUserRequest(
        String name,
        String emailAddress,
        String login,
        String password,
        UUID userTypeId) {

}
