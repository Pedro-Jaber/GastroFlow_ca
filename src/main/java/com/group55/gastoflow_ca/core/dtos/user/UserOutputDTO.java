package com.group55.gastoflow_ca.core.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;

public record UserOutputDTO(
        UUID id,
        String name,
        String emailAddress,
        String login,
        UserTypeDTO userTypeDTO,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}