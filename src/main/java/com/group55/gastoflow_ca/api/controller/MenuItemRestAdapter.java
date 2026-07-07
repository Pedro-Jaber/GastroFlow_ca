package com.group55.gastoflow_ca.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.core.controllers.MenuItemController;
import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;

@RestController
@RequestMapping("/menu-items")
public class MenuItemRestAdapter {

    private final MenuItemController menuItemController;

    public MenuItemRestAdapter(MenuItemController menuItemController) {
        this.menuItemController = menuItemController;
    }

    @PostMapping
    public ResponseEntity<MenuItemOutputDTO> create(
            @RequestBody MenuItemOutputDTO request) {

        CreateMenuItemInputDataDTO input = new CreateMenuItemInputDataDTO(
                request.name(),
                request.description(),
                request.price(),
                request.onlyInRestaurant(),
                request.photoPath(),
                request.restaurantId());

        MenuItemOutputDTO output = menuItemController.createMenuItem(input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

}
