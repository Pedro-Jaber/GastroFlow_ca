package com.group55.gastoflow_ca.core.usecases.menuItem;

import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class CreateMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;

    private CreateMenuItemUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
    }

    public static CreateMenuItemUseCase create(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway) {
        return new CreateMenuItemUseCase(menuItemGateway, restaurantGateway);
    }

    public MenuItem run(CreateMenuItemInputDataDTO input) {

        this.restaurantGateway.findById(input.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant with id " + input.restaurantId() + " not found."));

        final MenuItem menuItem = MenuItem.create(
                input.name(),
                input.description(),
                input.price(),
                input.onlyInRestaurant(),
                input.photoPath(),
                input.restaurantId());

        MenuItem savedMenuItem = this.menuItemGateway.saveNewMenuItem(menuItem);
        return savedMenuItem;
    }

}
