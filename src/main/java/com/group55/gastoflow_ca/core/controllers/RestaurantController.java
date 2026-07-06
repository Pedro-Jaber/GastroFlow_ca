package com.group55.gastoflow_ca.core.controllers;

import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantOutputDTO;
import com.group55.gastoflow_ca.core.gateways.RestaurantGateway;
import com.group55.gastoflow_ca.core.gateways.UserGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.presenters.RestaurantPresenter;
import com.group55.gastoflow_ca.core.usecases.restaurant.CreateRestaurantUseCase;

public class RestaurantController {

    private final IRestaurantDataSource restaurantDataSource;
    private final IUserDataSource userDataSource;

    private final RestaurantGateway restaurantGateway;

    private RestaurantController(IRestaurantDataSource restaurantDataSource, IUserDataSource userDataSource) {
        this.restaurantDataSource = restaurantDataSource;
        this.userDataSource = userDataSource;

        this.restaurantGateway = RestaurantGateway.create(this.restaurantDataSource);
    }

    public static RestaurantController create(IRestaurantDataSource restaurantDataSource,
            IUserDataSource userDataSource) {
        return new RestaurantController(restaurantDataSource, userDataSource);
    }

    public RestaurantOutputDTO createRestaurant(CreateRestaurantInputDataDTO input) {

        UserGateway userGateway = UserGateway.create(userDataSource);

        CreateRestaurantUseCase useCase = CreateRestaurantUseCase.create(this.restaurantGateway, userGateway);

        var restaurant = useCase.run(input);

        var restaurantOutDTO = RestaurantPresenter.toOutputDTO(restaurant);
        return restaurantOutDTO;

    }

}
