package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;

import com.group55.gastoflow_ca.core.entities.Restaurant;

public interface IRestaurantGateway {

    Restaurant saveNewRestaurant(Restaurant newRestaurant);

    Optional<Restaurant> findByName(String name);

}
