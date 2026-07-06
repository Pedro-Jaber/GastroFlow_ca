package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.restaurant.UpdateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

public class UpdateRestaurantUseCase {

    private final IRestaurantGateway restaurantGateway;
    private final IUserGateway userGateway;

    private UpdateRestaurantUseCase(IRestaurantGateway restaurantGateway, IUserGateway userGateway) {
        this.restaurantGateway = restaurantGateway;
        this.userGateway = userGateway;
    }

    public static UpdateRestaurantUseCase create(IRestaurantGateway restaurantGateway, IUserGateway userGateway) {
        return new UpdateRestaurantUseCase(restaurantGateway, userGateway);
    }

    public Restaurant run(UUID id, UpdateRestaurantInputDataDTO input) {

        final Restaurant existingRestaurant = this.restaurantGateway.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found."));

        if (input.name() != null && !input.name().isBlank()) {
            existingRestaurant.setName(input.name());
        }

        if (input.address() != null && !input.address().isBlank()) {
            existingRestaurant.setAddress(input.address());
        }

        if (input.cuisineType() != null && !input.cuisineType().isBlank()) {
            existingRestaurant.setCuisineType(input.cuisineType());
        }

        if (input.openingHours() != null && !input.openingHours().isBlank()) {
            existingRestaurant.setOpeningHours(input.openingHours());
        }

        if (input.ownerId() != null) {
            final User owner = this.userGateway.findById(input.ownerId())
                    .orElseThrow(() -> new UserNotFoundException("User with id " + input.ownerId() + " not found."));

            existingRestaurant.setOwner(owner);
        }

        existingRestaurant.setUpdatedAt(LocalDateTime.now());

        Restaurant savedRestaurant = this.restaurantGateway.updateRestaurant(existingRestaurant);

        return savedRestaurant;
    }

}
