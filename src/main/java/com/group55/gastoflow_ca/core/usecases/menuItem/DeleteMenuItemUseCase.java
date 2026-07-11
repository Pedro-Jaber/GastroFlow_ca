package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.util.UUID;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class DeleteMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;
    private final IRestaurantGateway restaurantGateway;

    private DeleteMenuItemUseCase(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway) {
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
    }

    public static DeleteMenuItemUseCase create(IMenuItemGateway menuItemGateway, IRestaurantGateway restaurantGateway) {
        return new DeleteMenuItemUseCase(menuItemGateway, restaurantGateway);
    }

    public void run(UserToken userToken, UUID id) {

        MenuItem menuItem = this.menuItemGateway.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("MenuItem with id " + id + " not found."));

        Restaurant restaurant = this.restaurantGateway.findById(menuItem.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant with id " + menuItem.getRestaurantId() + " not found."));

        boolean isOwner = restaurant.getOwnerId().equals(userToken.getUserId());

        if (isOwner) {
            AuthorizationChecker.requirePermission(userToken, Permission.DELETE_MENU_ITEM);
        } else {
            AuthorizationChecker.requirePermission(userToken, Permission.DELETE_ALL_MENU_ITEM);
        }

        this.menuItemGateway.deleteById(id);
    }

}
