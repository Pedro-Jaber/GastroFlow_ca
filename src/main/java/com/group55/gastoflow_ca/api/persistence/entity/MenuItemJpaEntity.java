package com.group55.gastoflow_ca.api.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu_item")
public class MenuItemJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Description is required")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    private BigDecimal price;

    @Column(name = "only_in_restaurant", nullable = false)
    @NotNull(message = "Only in restaurant is required")
    private boolean onlyInRestaurant;

    @Column(name = "photo_path", nullable = false)
    @NotBlank(message = "Photo path is required")
    private String photoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @NotNull(message = "Restaurant is required")
    private RestaurantJpaEntity restaurant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
