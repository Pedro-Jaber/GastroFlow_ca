package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;

public interface IRestaurantGateway {

    Restaurant saveNewRestaurant(Restaurant newRestaurant);

    PageOutputDTO<Restaurant> findAll(PageInputDTO pageInput);

    Optional<Restaurant> findById(UUID id);

    Optional<Restaurant> findByName(String name);

    Restaurant updateRestaurant(Restaurant existingRestaurant);

    void deleteById(UUID id);
}
