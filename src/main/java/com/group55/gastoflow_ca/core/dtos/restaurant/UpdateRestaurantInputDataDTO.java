package com.group55.gastoflow_ca.core.dtos.restaurant;

import java.util.UUID;

public record UpdateRestaurantInputDataDTO(
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UUID ownerId) {

}
