package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.Optional;

import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;

public interface IRestaurantDataSource {

    RestaurantDTO saveNewRestaurant(RestaurantDTO newRestaurantDTO);

    Optional<RestaurantDTO> findByName(String name);

}
