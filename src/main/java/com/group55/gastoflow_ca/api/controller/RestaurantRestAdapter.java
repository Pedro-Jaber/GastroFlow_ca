package com.group55.gastoflow_ca.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.restaurant.CreateRestaurantRequest;
import com.group55.gastoflow_ca.core.controllers.RestaurantController;
import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantOutputDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/restaurants")
public class RestaurantRestAdapter {

    private final RestaurantController restaurantController;

    public RestaurantRestAdapter(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @PostMapping
    public ResponseEntity<RestaurantOutputDTO> create(
            @RequestBody @Valid CreateRestaurantRequest request) {
        CreateRestaurantInputDataDTO input = new CreateRestaurantInputDataDTO(
                request.name(),
                request.address(),
                request.cuisineType(),
                request.openingHours(),
                request.ownerId());

        System.out.println("input:" + input);

        RestaurantOutputDTO output = restaurantController.createRestaurant(input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
}
