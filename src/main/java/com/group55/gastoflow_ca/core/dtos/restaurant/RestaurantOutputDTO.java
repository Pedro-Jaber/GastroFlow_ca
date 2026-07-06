package com.group55.gastoflow_ca.core.dtos.restaurant;

import java.time.LocalDateTime;
import java.util.UUID;

public record RestaurantOutputDTO(
        UUID id,
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
