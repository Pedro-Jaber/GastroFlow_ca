package com.group55.gastoflow_ca.core.gateways;

import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IMenuItemDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

public class MenuItemGateway implements IMenuItemGateway {

    private final IMenuItemDataSource menuItemDataSource;

    private MenuItemGateway(IMenuItemDataSource menuItemDataSource) {
        this.menuItemDataSource = menuItemDataSource;
    }

    public static IMenuItemGateway create(IMenuItemDataSource menuItemDataSource) {
        return new MenuItemGateway(menuItemDataSource);
    }

    @Override
    public MenuItem saveNewMenuItem(MenuItem menuItem) {
        final MenuItemDTO menuItemDTO = toDTO(menuItem);
        final MenuItemDTO savedMenuItemDTO = this.menuItemDataSource.saveNewMenuItem(menuItemDTO);
        return toEntity(savedMenuItemDTO);
    }

    private MenuItem toEntity(MenuItemDTO menuItemDTO) {
        return MenuItem.create(
                menuItemDTO.id(),
                menuItemDTO.name(),
                menuItemDTO.description(),
                menuItemDTO.price(),
                menuItemDTO.onlyInRestaurant(),
                menuItemDTO.photoPath(),
                menuItemDTO.restaurantId(),
                menuItemDTO.createdAt(),
                menuItemDTO.updatedAt());
    }

    private MenuItemDTO toDTO(MenuItem menuItem) {
        return new MenuItemDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isOnlyInRestaurant(),
                menuItem.getPhotoPath(),
                menuItem.getRestaurantId(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt());
    }

}
