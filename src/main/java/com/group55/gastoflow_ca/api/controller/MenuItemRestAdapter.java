package com.group55.gastoflow_ca.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group55.gastoflow_ca.api.dto.request.menuItem.CreateMenuItemRequest;
import com.group55.gastoflow_ca.api.dto.request.menuItem.UpdateMenuItemRequest;
import com.group55.gastoflow_ca.core.controllers.MenuItemController;
import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.UpdateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;

@RestController
@RequestMapping("/menu-items")
public class MenuItemRestAdapter {

    private final MenuItemController menuItemController;

    public MenuItemRestAdapter(MenuItemController menuItemController) {
        this.menuItemController = menuItemController;
    }

    @PostMapping
    public ResponseEntity<MenuItemOutputDTO> create(
            @AuthenticationPrincipal UserToken userToken,
            @RequestBody CreateMenuItemRequest request) {

        CreateMenuItemInputDataDTO input = new CreateMenuItemInputDataDTO(
                request.name(),
                request.description(),
                request.price(),
                request.onlyInRestaurant(),
                request.photoPath(),
                request.restaurantId());

        MenuItemOutputDTO output = menuItemController.createMenuItem(userToken, input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping
    public ResponseEntity<PageOutputDTO<MenuItemOutputDTO>> getAll(
            @AuthenticationPrincipal UserToken userToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageInputDTO pageInput = new PageInputDTO(page, size);

        PageOutputDTO<MenuItemOutputDTO> output = menuItemController.getAllMenuItems(userToken, pageInput);

        return ResponseEntity.ok(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemOutputDTO> getById(
            @AuthenticationPrincipal UserToken userToken,
            @PathVariable UUID id) {
        MenuItemOutputDTO output = menuItemController.getMenuItemById(userToken, id);

        return ResponseEntity.ok(output);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemOutputDTO> update(
            @AuthenticationPrincipal UserToken userToken,
            @PathVariable UUID id,
            @RequestBody UpdateMenuItemRequest request) {

        UpdateMenuItemInputDataDTO input = new UpdateMenuItemInputDataDTO(
                request.name(),
                request.description(),
                request.price(),
                request.onlyInRestaurant(),
                request.photoPath(),
                request.restaurantId());

        MenuItemOutputDTO output = menuItemController.updateMenuItem(userToken, id, input);

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserToken userToken,
            @PathVariable UUID id) {
        menuItemController.deleteMenuItem(userToken, id);
        return ResponseEntity.noContent().build();
    }

}
