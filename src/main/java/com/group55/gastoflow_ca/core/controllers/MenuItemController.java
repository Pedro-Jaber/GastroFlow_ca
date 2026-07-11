package com.group55.gastoflow_ca.core.controllers;

import java.util.List;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.menu_item.CreateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemOutputDTO;
import com.group55.gastoflow_ca.core.dtos.menu_item.UpdateMenuItemInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.gateways.MenuItemGateway;
import com.group55.gastoflow_ca.core.gateways.RestaurantGateway;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IMenuItemDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;
import com.group55.gastoflow_ca.core.presenters.MenuItemPresenter;
import com.group55.gastoflow_ca.core.usecases.menuItem.CreateMenuItemUseCase;
import com.group55.gastoflow_ca.core.usecases.menuItem.DeleteMenuItemUseCase;
import com.group55.gastoflow_ca.core.usecases.menuItem.GetAllMenuItemsUseCase;
import com.group55.gastoflow_ca.core.usecases.menuItem.GetMenuItemByIdUseCase;
import com.group55.gastoflow_ca.core.usecases.menuItem.UpdateMenuItemUseCase;

public class MenuItemController {

    private final IMenuItemDataSource menuItemDataSource;
    private final IRestaurantDataSource restaurantDataSource;

    private final IMenuItemGateway menuItemGateway;

    private MenuItemController(IMenuItemDataSource menuItemDataSource, IRestaurantDataSource restaurantDataSource) {
        this.menuItemDataSource = menuItemDataSource;
        this.restaurantDataSource = restaurantDataSource;

        this.menuItemGateway = MenuItemGateway.create(this.menuItemDataSource);
    }

    public static MenuItemController create(IMenuItemDataSource menuItemDataSource,
            IRestaurantDataSource restaurantDataSource) {
        return new MenuItemController(menuItemDataSource, restaurantDataSource);
    }

    public MenuItemOutputDTO createMenuItem(UserToken userToken, CreateMenuItemInputDataDTO input) {

        RestaurantGateway restaurantGateway = RestaurantGateway.create(this.restaurantDataSource);

        CreateMenuItemUseCase useCase = CreateMenuItemUseCase.create(this.menuItemGateway, restaurantGateway);
        MenuItem menuItem = useCase.run(userToken, input);

        MenuItemOutputDTO menuItemOutDTO = MenuItemPresenter.toOutputDTO(menuItem);
        return menuItemOutDTO;
    }

    public PageOutputDTO<MenuItemOutputDTO> getAllMenuItems(UserToken userToken, PageInputDTO pageInput) {

        GetAllMenuItemsUseCase useCase = GetAllMenuItemsUseCase.create(this.menuItemGateway);

        PageOutputDTO<MenuItem> page = useCase.run(userToken, pageInput);

        List<MenuItemOutputDTO> content = page.content().stream()
                .map(MenuItemPresenter::toOutputDTO)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());

    }

    public MenuItemOutputDTO getMenuItemById(UserToken userToken, UUID id) {

        RestaurantGateway restaurantGateway = RestaurantGateway.create(this.restaurantDataSource);
        GetMenuItemByIdUseCase useCase = GetMenuItemByIdUseCase.create(this.menuItemGateway, restaurantGateway);

        MenuItem menuItem = useCase.run(userToken, id);

        MenuItemOutputDTO menuItemOutDTO = MenuItemPresenter.toOutputDTO(menuItem);
        return menuItemOutDTO;
    }

    public MenuItemOutputDTO updateMenuItem(UserToken userToken, UUID id, UpdateMenuItemInputDataDTO input) {

        RestaurantGateway restaurantGateway = RestaurantGateway.create(this.restaurantDataSource);
        UpdateMenuItemUseCase useCase = UpdateMenuItemUseCase.create(this.menuItemGateway, restaurantGateway);

        MenuItem menuItem = useCase.run(userToken, id, input);

        MenuItemOutputDTO menuItemOutDTO = MenuItemPresenter.toOutputDTO(menuItem);
        return menuItemOutDTO;
    }

    public void deleteMenuItem(UserToken userToken, UUID id) {

        RestaurantGateway restaurantGateway = RestaurantGateway.create(this.restaurantDataSource);
        DeleteMenuItemUseCase useCase = DeleteMenuItemUseCase.create(this.menuItemGateway, restaurantGateway);

        useCase.run(userToken, id);
    }

}
