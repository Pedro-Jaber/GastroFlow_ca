package com.group55.gastoflow_ca.core.usecases.menuItem;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

public class GetAllMenuItemsUseCase {

    private final IMenuItemGateway menuItemGateway;

    private GetAllMenuItemsUseCase(IMenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public static GetAllMenuItemsUseCase create(IMenuItemGateway menuItemGateway) {
        return new GetAllMenuItemsUseCase(menuItemGateway);
    }

    public PageOutputDTO<MenuItem> run(PageInputDTO pageInput) {
        return this.menuItemGateway.findAll(pageInput);
    }

}
