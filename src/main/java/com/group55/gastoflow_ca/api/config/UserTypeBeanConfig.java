package com.group55.gastoflow_ca.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.group55.gastoflow_ca.core.controllers.UserTypeController;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;

@Configuration
public class UserTypeBeanConfig {

    @Bean
    public UserTypeController userTypeController(IUserTypeDataSource userTypeDataSource) {
        return UserTypeController.create(userTypeDataSource);
    }
}
