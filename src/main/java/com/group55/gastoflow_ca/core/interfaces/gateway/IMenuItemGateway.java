package com.group55.gastoflow_ca.core.interfaces.gateway;

import java.util.Optional;
import java.util.UUID;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.MenuItem;

public interface IMenuItemGateway {

    MenuItem saveNewMenuItem(MenuItem menuItem);

    PageOutputDTO<MenuItem> findAll(PageInputDTO pageInput);

    Optional<MenuItem> findById(UUID id);

    MenuItem updateMenuItem(MenuItem existingMenuItem);

}
