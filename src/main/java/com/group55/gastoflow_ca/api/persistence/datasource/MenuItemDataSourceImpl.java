package com.group55.gastoflow_ca.api.persistence.datasource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.api.persistence.entity.MenuItemJpaEntity;
import com.group55.gastoflow_ca.api.persistence.entity.RestaurantJpaEntity;
import com.group55.gastoflow_ca.api.persistence.repository.MenuItemJpaRepository;
import com.group55.gastoflow_ca.api.persistence.repository.RestaurantJpaRepository;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
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

    @Override
    public PageOutputDTO<MenuItemDTO> findAll(PageInputDTO pageInput) {

        Pageable pageable = PageRequest.of(pageInput.page(), pageInput.size());
        Page<MenuItemJpaEntity> page = menuItemJpaRepository.findAll(pageable);

        List<MenuItemDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public Optional<MenuItemDTO> findById(UUID id) {
        return this.menuItemJpaRepository.findById(id).map(this::toDTO);
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
