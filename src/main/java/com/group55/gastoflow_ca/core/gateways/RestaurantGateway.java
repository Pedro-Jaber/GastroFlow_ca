package com.group55.gastoflow_ca.core.gateways;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

public class RestaurantGateway implements IRestaurantGateway {

    private final IRestaurantDataSource dataSource;

    private RestaurantGateway(IRestaurantDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RestaurantGateway create(IRestaurantDataSource dataSource) {
        return new RestaurantGateway(dataSource);
    }

    @Override
    public Restaurant saveNewRestaurant(Restaurant newRestaurant) {
        final RestaurantDTO newRestaurantDTO = toDTO(newRestaurant);
        final RestaurantDTO savedRestaurantDTO = this.dataSource.saveNewRestaurant(newRestaurantDTO);
        return toEntity(savedRestaurantDTO);
    }

    @Override
    public PageOutputDTO<Restaurant> findAll(PageInputDTO pageInput) {

        PageOutputDTO<RestaurantDTO> page = this.dataSource.findAll(pageInput);

        List<Restaurant> content = page.content().stream()
                .map(this::toEntity)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return this.dataSource.findById(id).map(this::toEntity);
    }

    @Override
    public Optional<Restaurant> findByName(String name) {
        return this.dataSource.findByName(name).map(this::toEntity);
    }

    @Override
    public Restaurant updateRestaurant(Restaurant existingRestaurant) {
        final RestaurantDTO existingRestaurantDTO = toDTO(existingRestaurant);
        final RestaurantDTO updatedRestaurantDTO = this.dataSource.updateRestaurant(existingRestaurantDTO);
        return toEntity(updatedRestaurantDTO);
    }

    @Override
    public void deleteById(UUID id) {
        this.dataSource.deleteById(id);
    }

    private Restaurant toEntity(RestaurantDTO restaurantDTO) {
        return Restaurant.create(
                restaurantDTO.id(),
                restaurantDTO.name(),
                restaurantDTO.address(),
                restaurantDTO.cuisineType(),
                restaurantDTO.openingHours(),
                restaurantDTO.ownerId(),
                restaurantDTO.createdAt(),
                restaurantDTO.updatedAt());
    }

    private RestaurantDTO toDTO(Restaurant restaurant) {
        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getOwnerId(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt());
    }

}
