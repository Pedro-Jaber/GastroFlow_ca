package com.group55.gastoflow_ca.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.restaurant.CreateRestaurantRequest;
import com.group55.gastoflow_ca.api.dto.request.restaurant.UpdateRestaurantRequest;
import com.group55.gastoflow_ca.core.controllers.RestaurantController;
import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantOutputDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.UpdateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;

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

    @GetMapping
    public ResponseEntity<PageOutputDTO<RestaurantOutputDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageInputDTO pageInput = new PageInputDTO(page, size);

        PageOutputDTO<RestaurantOutputDTO> output = restaurantController.getAllRestaurants(pageInput);

        return ResponseEntity.ok(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantOutputDTO> getById(@PathVariable UUID id) {
        RestaurantOutputDTO output = restaurantController.getRestaurantById(id);

        return ResponseEntity.ok(output);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantOutputDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateRestaurantRequest request) {

        UpdateRestaurantInputDataDTO input = new UpdateRestaurantInputDataDTO(
                request.name(),
                request.address(),
                request.cuisineType(),
                request.openingHours(),
                request.ownerId());

        RestaurantOutputDTO output = restaurantController.updateRestaurant(id, input);

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        restaurantController.deleteRestaurant(id);

        return ResponseEntity.noContent().build();
    }

}
