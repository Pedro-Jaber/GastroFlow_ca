package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.util.UUID;

import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

public class DeleteMenuItemUseCase {

    private final IMenuItemGateway menuItemGateway;

    private DeleteMenuItemUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public static DeleteMenuItemUseCase create(IMenuItemGateway menuItemGateway) {
        return new DeleteMenuItemUseCase(menuItemGateway);
    }

    public void run(UUID id) {
        this.menuItemGateway.deleteById(id);
    }

}
