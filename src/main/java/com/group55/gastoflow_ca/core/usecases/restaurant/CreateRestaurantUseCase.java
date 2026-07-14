package com.group55.gastoflow_ca.core.usecases.restaurant;

import com.group55.gastoflow_ca.core.auth.AuthorizationChecker;
import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.RestaurantAlreadyExistsException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class CreateRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final IUserGateway userGateway;

    private CreateRestaurantUseCase(IRestaurantGateway restaurantGateway, IUserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    public static CreateRestaurantUseCase create(IRestaurantGateway restaurantGateway, IUserGateway userGateway) {
        return new CreateRestaurantUseCase(restaurantGateway, userGateway);
    }

    public Restaurant run(UserToken userToken, CreateRestaurantInputDataDTO input) {

        boolean isOwner = input.ownerId().equals(userToken.getUserId());

        if (isOwner) {
            AuthorizationChecker.requirePermission(userToken, Permission.CREATE_RESTAURANT);
        } else {
            AuthorizationChecker.requirePermission(userToken, Permission.CREATE_ALL_RESTAURANT);
        }

        this.restaurantGateway.findByName(input.name())
                .ifPresent(r -> {
                    throw new RestaurantAlreadyExistsException(
                            "Restaurant with name " + r.getName() + " already exists.");
                });

        final User owner = this.userGateway.findById(input.ownerId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + input.ownerId() + " not found."));

        final Restaurant newRestaurant = Restaurant.create(input.name(), input.address(), input.cuisineType(),
                input.openingHours(), owner.getId());

        Restaurant savedRestaurant = this.restaurantGateway.saveNewRestaurant(newRestaurant);
        return savedRestaurant;
    }
}
