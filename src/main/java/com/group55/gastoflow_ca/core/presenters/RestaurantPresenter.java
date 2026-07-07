package com.group55.gastoflow_ca.core.presenters;

import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantOutputDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;

public class RestaurantPresenter {

    public static RestaurantOutputDTO toOutputDTO(Restaurant restaurant) {
        RestaurantOutputDTO restaurantOutputDTO = new RestaurantOutputDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt());
        return restaurantOutputDTO;
    }

}
