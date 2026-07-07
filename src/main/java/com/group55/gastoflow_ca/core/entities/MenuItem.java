package com.group55.gastoflow_ca.core.entities;

import java.math.BigDecimal;
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
public class MenuItem {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean onlyInRestaurant;
    private String photoPath;
    private UUID restaurantId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MenuItem create(
            String name,
            String description,
            BigDecimal price,
            boolean onlyInRestaurant,
            String photoPath,
            UUID restaurantId) {

        MenuItem item = new MenuItem();

        item.id = UUID.randomUUID();
        item.name = name;
        item.description = description;
        item.price = price;
        item.onlyInRestaurant = onlyInRestaurant;
        item.photoPath = photoPath;
        item.restaurantId = restaurantId;
        item.createdAt = LocalDateTime.now();
        item.updatedAt = LocalDateTime.now();

        return item;
    }

    public static MenuItem create(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            boolean onlyInRestaurant,
            String photoPath,
            UUID restaurantId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        MenuItem item = new MenuItem();

        item.id = id;
        item.name = name;
        item.description = description;
        item.price = price;
        item.onlyInRestaurant = onlyInRestaurant;
        item.photoPath = photoPath;
        item.restaurantId = restaurantId;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;

        return item;
    }
}