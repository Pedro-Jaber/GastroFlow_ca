package com.group55.gastoflow_ca.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.group55.gastoflow_ca.core.controllers.MenuItemController;
import com.group55.gastoflow_ca.core.controllers.RestaurantController;
import com.group55.gastoflow_ca.core.controllers.UserController;
import com.group55.gastoflow_ca.core.controllers.UserTypeController;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IMenuItemDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;

@Configuration
public class BeanConfig {

    @Bean
    public UserTypeController userTypeController(IUserTypeDataSource userTypeDataSource) {
        return UserTypeController.create(userTypeDataSource);
    }

    @Bean
    public UserController userController(IUserDataSource userDataSource, IUserTypeDataSource userTypeDataSource) {
        return UserController.create(userDataSource, userTypeDataSource);
    }

    @Bean
    public RestaurantController restaurantController(IRestaurantDataSource restaurantDataSource,
            IUserDataSource userDataSource) {
        return RestaurantController.create(restaurantDataSource, userDataSource);
    }

    @Bean
    public MenuItemController menuItemController(IMenuItemDataSource menuItemDataSource,
            IRestaurantDataSource restaurantDataSource) {
        return MenuItemController.create(menuItemDataSource, restaurantDataSource);
    }
}
