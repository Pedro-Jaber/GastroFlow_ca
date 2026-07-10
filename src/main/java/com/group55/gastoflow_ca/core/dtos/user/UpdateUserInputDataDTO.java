package com.group55.gastoflow_ca.core.dtos.user;

import java.util.UUID;

public record UpdateUserInputDataDTO(
        String name,
        String emailAddress,
        String login,
        String password,
        UUID userTypeId) {

}
