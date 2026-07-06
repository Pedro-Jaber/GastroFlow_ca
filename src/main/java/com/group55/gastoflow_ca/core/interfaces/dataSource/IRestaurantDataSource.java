package com.group55.gastoflow_ca.core.interfaces.dataSource;

import java.util.Optional;

import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;

public interface IRestaurantDataSource {

    RestaurantDTO saveNewRestaurant(RestaurantDTO newRestaurantDTO);

    PageOutputDTO<RestaurantDTO> findAll(PageInputDTO pageInput);

    Optional<RestaurantDTO> findByName(String name);

}
