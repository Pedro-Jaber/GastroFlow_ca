package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.util.UUID;

import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.exceptions.MenuItemNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

public class GetMenuItemByIdUseCase {

    private final IMenuItemGateway menuItemGateway;

    private GetMenuItemByIdUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public static GetMenuItemByIdUseCase create(IMenuItemGateway menuItemGateway) {
        return new GetMenuItemByIdUseCase(menuItemGateway);
    }

    public MenuItem run(UUID id) {
        return this.menuItemGateway.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException("MenuItem with id " + id + " not found."));
    }

}
