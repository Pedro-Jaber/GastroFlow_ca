package com.group55.gastoflow_ca.api.dto.request.menuItem;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMenuItemRequest(
        String name,
        String description,
        BigDecimal price,
        Boolean onlyInRestaurant,
        String photoPath,
        UUID restaurantId) {

}
