package com.group55.gastoflow_ca.core.gateways;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
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

    private Restaurant toEntity(RestaurantDTO restaurantDTO) {
        return Restaurant.create(
                restaurantDTO.id(),
                restaurantDTO.name(),
                restaurantDTO.address(),
                restaurantDTO.cuisineType(),
                restaurantDTO.openingHours(),
                User.create(
                        restaurantDTO.ownerDTO().id(),
                        restaurantDTO.ownerDTO().name(),
                        restaurantDTO.ownerDTO().emailAddress(),
                        restaurantDTO.ownerDTO().login(),
                        restaurantDTO.ownerDTO().password(),
                        UserType.create(
                                restaurantDTO.ownerDTO().userTypeDTO().id(),
                                restaurantDTO.ownerDTO().userTypeDTO().name(),
                                restaurantDTO.ownerDTO().userTypeDTO().permissions()),
                        restaurantDTO.ownerDTO().createdAt(),
                        restaurantDTO.ownerDTO().updatedAt()),
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
                new UserDTO(
                        restaurant.getOwner().getId(),
                        restaurant.getOwner().getName(),
                        restaurant.getOwner().getEmailAddress(),
                        restaurant.getOwner().getLogin(),
                        restaurant.getOwner().getPassword(),
                        new UserTypeDTO(
                                restaurant.getOwner().getUserType().getId(),
                                restaurant.getOwner().getUserType().getName(),
                                restaurant.getOwner().getUserType().getPermissions()),
                        restaurant.getOwner().getCreatedAt(),
                        restaurant.getOwner().getUpdatedAt()),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt());
    }

}
