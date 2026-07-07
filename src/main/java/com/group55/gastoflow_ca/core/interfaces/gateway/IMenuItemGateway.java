package com.group55.gastoflow_ca.core.interfaces.gateway;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;

public interface IMenuItemGateway {

    MenuItem saveNewMenuItem(MenuItem menuItem);

    PageOutputDTO<MenuItem> findAll(PageInputDTO pageInput);

}
