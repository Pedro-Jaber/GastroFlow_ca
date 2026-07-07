package com.group55.gastoflow_ca.core.interfaces.dataSource;

import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemDTO;

public interface IMenuItemDataSource {

    MenuItemDTO saveNewMenuItem(MenuItemDTO menuItemDTO);

}
