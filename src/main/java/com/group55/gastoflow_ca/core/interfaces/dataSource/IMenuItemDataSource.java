package com.group55.gastoflow_ca.core.interfaces.dataSource;

import com.group55.gastoflow_ca.core.dtos.menu_item.MenuItemDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;

public interface IMenuItemDataSource {

    MenuItemDTO saveNewMenuItem(MenuItemDTO menuItemDTO);

    PageOutputDTO<MenuItemDTO> findAll(PageInputDTO pageInput);

}
