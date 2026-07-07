package com.group55.gastoflow_ca.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.core.controllers.MenuItemController;
import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;

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

    @GetMapping
    public ResponseEntity<PageOutputDTO<MenuItemOutputDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageInputDTO pageInput = new PageInputDTO(page, size);

        PageOutputDTO<MenuItemOutputDTO> output = menuItemController.getAllMenuItems(pageInput);

        return ResponseEntity.ok(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemOutputDTO> getById(
            @PathVariable UUID id) {
        MenuItemOutputDTO output = menuItemController.getMenuItemById(id);

        return ResponseEntity.ok(output);
    }

}
