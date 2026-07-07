package com.group55.gastoflow_ca.core.presenters;

import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;

public class MenuItemPresenter {

    public static MenuItemOutputDTO toOutputDTO(MenuItem menuItem) {
        MenuItemOutputDTO menuItemOutputDTO = new MenuItemOutputDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isOnlyInRestaurant(),
                menuItem.getPhotoPath(),
                menuItem.getRestaurantId(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt());

        return menuItemOutputDTO;
    }
}
