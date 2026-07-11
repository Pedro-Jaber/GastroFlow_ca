package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.util.UUID;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class DeleteRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;

    private DeleteRestaurantUseCase(IRestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    public static DeleteRestaurantUseCase create(IRestaurantGateway restaurantGateway) {
        return new DeleteRestaurantUseCase(restaurantGateway);
    }

    public void run(UserToken userToken, UUID id) {

        Restaurant restaurant = this.restaurantGateway.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found."));

        boolean isOwner = restaurant.getOwnerId().equals(userToken.getUserId());

        if (isOwner) {
            AuthorizationChecker.requirePermission(userToken, Permission.DELETE_RESTAURANT);
        } else {
            AuthorizationChecker.requirePermission(userToken, Permission.DELETE_ALL_RESTAURANT);
        }

        this.restaurantGateway.deleteById(id);
    }

}
