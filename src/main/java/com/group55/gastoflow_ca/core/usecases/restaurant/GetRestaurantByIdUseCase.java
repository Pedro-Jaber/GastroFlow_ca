package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.util.UUID;

import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class GetRestaurantByIdUseCase {

    private final IRestaurantGateway restaurantGateway;

    private GetRestaurantByIdUseCase(IRestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    public static GetRestaurantByIdUseCase create(IRestaurantGateway restaurantGateway) {
        return new GetRestaurantByIdUseCase(restaurantGateway);
    }

    public Restaurant run(UUID id) {
        return this.restaurantGateway.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found."));
    }
}
