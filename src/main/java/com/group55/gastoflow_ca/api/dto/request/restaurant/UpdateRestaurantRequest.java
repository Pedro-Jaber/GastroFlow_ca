package com.group55.gastoflow_ca.api.dto.request.restaurant;

import java.util.UUID;

public record UpdateRestaurantRequest(
        String name,
        String address,
        String cuisineType,
        String openingHours,
        UUID ownerId) {

}
