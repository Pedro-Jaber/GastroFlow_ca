package com.group55.gastoflow_ca.core.usecases.restaurant;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class GatAllRestaurantsUseCase {

    private final IRestaurantGateway restaurantGateway;

    private GatAllRestaurantsUseCase(IRestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    public static GatAllRestaurantsUseCase create(IRestaurantGateway restaurantGateway) {
        return new GatAllRestaurantsUseCase(restaurantGateway);
    }

    public PageOutputDTO<Restaurant> run(PageInputDTO pageInput) {
        return this.restaurantGateway.findAll(pageInput);
    }
}
