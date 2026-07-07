package com.group55.gastoflow_ca.core.dtos.menu_item;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateMenuItemInputDataDTO(
        String name,
        String description,
        BigDecimal price,
        Boolean onlyInRestaurant,
        String photoPath,
        UUID restaurantId) {

}
