package com.group55.gastoflow_ca.core.dtos.restaurant;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.user.UserDTO;

public record RestaurantDTO(
        UUID id,
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UserDTO ownerDTO,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
