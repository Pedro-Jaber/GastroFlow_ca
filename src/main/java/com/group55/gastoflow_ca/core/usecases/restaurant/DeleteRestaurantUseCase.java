package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.util.UUID;

import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class DeleteRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;

    private DeleteRestaurantUseCase(IRestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    public static DeleteRestaurantUseCase create(IRestaurantGateway restaurantGateway) {
        return new DeleteRestaurantUseCase(restaurantGateway);
    }

    public void run(UUID id) {
        this.restaurantGateway.deleteById(id);
    }

}
