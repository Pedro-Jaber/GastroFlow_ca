package com.group55.gastoflow_ca.core.dtos.menu_item;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMenuItemInputDataDTO(
                String name,
                String description,
                BigDecimal price,
                boolean onlyInRestaurant,
                String photoPath,
                UUID restaurantId) {

}
