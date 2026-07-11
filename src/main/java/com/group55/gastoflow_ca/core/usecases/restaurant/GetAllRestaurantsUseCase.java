package com.group55.gastoflow_ca.core.usecases.restaurant;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class GetAllRestaurantsUseCase {

    private final IRestaurantGateway restaurantGateway;

    private GetAllRestaurantsUseCase(IRestaurantGateway restaurantGateway) {
        this.restaurantGateway = restaurantGateway;
    }

    public static GetAllRestaurantsUseCase create(IRestaurantGateway restaurantGateway) {
        return new GetAllRestaurantsUseCase(restaurantGateway);
    }

    public PageOutputDTO<Restaurant> run(UserToken userToken, PageInputDTO pageInput) {

        AuthorizationChecker.requirePermission(userToken, Permission.READ_ALL_RESTAURANT);

        return this.restaurantGateway.findAll(pageInput);
    }
}
