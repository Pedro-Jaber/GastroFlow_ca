package com.group55.gastoflow_ca.core.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Restaurant {

    private UUID id;
    private String name;
    private String address;
    private String cuisineType;
    private String openingHours;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Restaurant create(String name, String address, String cuisineType, String openingHours,
            UUID ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.id = UUID.randomUUID();
        restaurant.name = name;
        restaurant.address = address;
        restaurant.cuisineType = cuisineType;
        restaurant.openingHours = openingHours;
        restaurant.ownerId = ownerId;
        restaurant.createdAt = LocalDateTime.now();
        restaurant.updatedAt = LocalDateTime.now();
        return restaurant;
    }

    public static Restaurant create(UUID id, String name, String address, String cuisineType, String openingHours,
            UUID ownerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        Restaurant restaurant = new Restaurant();
        restaurant.id = id;
        restaurant.name = name;
        restaurant.address = address;
        restaurant.cuisineType = cuisineType;
        restaurant.openingHours = openingHours;
        restaurant.ownerId = ownerId;
        restaurant.createdAt = createdAt;
        restaurant.updatedAt = updatedAt;
        return restaurant;
    }
}