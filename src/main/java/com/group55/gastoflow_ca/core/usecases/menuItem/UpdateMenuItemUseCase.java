package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.menu_item.UpdateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class UpdateMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;

    private UpdateMenuItemUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
    }

    public static UpdateMenuItemUseCase create(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway) {
        return new UpdateMenuItemUseCase(menuItemGateway, restaurantGateway);
    }

    public MenuItem run(UUID id, UpdateMenuItemInputDataDTO input) {
        MenuItem existingMenuItem = this.menuItemGateway.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id.toString()));

        if (input.name() != null) {
            existingMenuItem.setName(input.name());
        }
        if (input.description() != null) {
            existingMenuItem.setDescription(input.description());
        }
        if (input.price() != null) {
            existingMenuItem.setPrice(input.price());
        }
        if (input.onlyInRestaurant() != null) {
            existingMenuItem.setOnlyInRestaurant(input.onlyInRestaurant());
        }
        if (input.photoPath() != null) {
            existingMenuItem.setPhotoPath(input.photoPath());
        }
        if (input.restaurantId() != null) {
            final Restaurant restaurant = this.restaurantGateway.findById(input.restaurantId())
                    .orElseThrow(() -> new RestaurantNotFoundException(
                            "Restaurant with id " + input.restaurantId() + " not found."));

            existingMenuItem.setRestaurantId(restaurant.getId());
        }

        existingMenuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem updatedMenuItem = this.menuItemGateway.updateMenuItem(existingMenuItem);

        return updatedMenuItem;
    }
}
