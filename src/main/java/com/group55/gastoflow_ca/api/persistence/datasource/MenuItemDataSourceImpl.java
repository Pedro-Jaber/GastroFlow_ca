package com.group55.gastoflow_ca.api.persistence.datasource;

import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.api.persistence.entity.MenuItemJpaEntity;
import com.group55.gastoflow_ca.api.persistence.entity.RestaurantJpaEntity;
import com.group55.gastoflow_ca.api.persistence.repository.MenuItemJpaRepository;
import com.group55.gastoflow_ca.api.persistence.repository.RestaurantJpaRepository;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemDTO;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IMenuItemDataSource;

@Component
public class MenuItemDataSourceImpl implements IMenuItemDataSource {

    private final MenuItemJpaRepository menuItemJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;

    public MenuItemDataSourceImpl(MenuItemJpaRepository menuItemJpaRepository,
            RestaurantJpaRepository restaurantJpaRepository) {
        this.menuItemJpaRepository = menuItemJpaRepository;
        this.restaurantJpaRepository = restaurantJpaRepository;
    }

    @Override
    public MenuItemDTO saveNewMenuItem(MenuItemDTO menuItemDTO) {
        MenuItemJpaEntity menuItemJpaEntity = toEntity(menuItemDTO);
        MenuItemJpaEntity saved = this.menuItemJpaRepository.save(menuItemJpaEntity);
        return toDTO(saved);
    }

    private MenuItemJpaEntity toEntity(MenuItemDTO menuItemDTO) {
        RestaurantJpaEntity restaurantJpaEntity = this.restaurantJpaRepository
                .findById(menuItemDTO.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant with id " + menuItemDTO.restaurantId() + " not found."));

        return new MenuItemJpaEntity(
                menuItemDTO.id(),
                menuItemDTO.name(),
                menuItemDTO.description(),
                menuItemDTO.price(),
                menuItemDTO.onlyInRestaurant(),
                menuItemDTO.photoPath(),
                restaurantJpaEntity,
                menuItemDTO.createdAt(),
                menuItemDTO.updatedAt());

    }

    private MenuItemDTO toDTO(MenuItemJpaEntity menuItemJpaEntity) {
        return new MenuItemDTO(
                menuItemJpaEntity.getId(),
                menuItemJpaEntity.getName(),
                menuItemJpaEntity.getDescription(),
                menuItemJpaEntity.getPrice(),
                menuItemJpaEntity.isOnlyInRestaurant(),
                menuItemJpaEntity.getPhotoPath(),
                menuItemJpaEntity.getRestaurant().getId(),
                menuItemJpaEntity.getCreatedAt(),
                menuItemJpaEntity.getUpdatedAt());
    }
}
