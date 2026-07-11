package com.group55.gastoflow_ca.api.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.gateways.UserGateway;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final String ADMIN_USERTYPE_NAME = "Admin";
    private static final String ADMIN_LOGIN = "admin";

    private final IUserTypeDataSource userTypeDataSource;
    private final IUserDataSource userDataSource;
    private final IPasswordHasher passwordHasher;

    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(
            IUserTypeDataSource userTypeDataSource,
            IUserDataSource userDataSource,
            IPasswordHasher passwordHasher,
            @Value("${admin.name:Administrador}") String adminName,
            @Value("${admin.email:admin@gastoflow.com}") String adminEmail,
            @Value("${admin.password:admin123}") String adminPassword) {
        this.userTypeDataSource = userTypeDataSource;
        this.userDataSource = userDataSource;
        this.passwordHasher = passwordHasher;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        UserTypeGateway userTypeGateway = UserTypeGateway.create(userTypeDataSource);
        UserGateway userGateway = UserGateway.create(userDataSource);

        UserType adminUserType = userTypeGateway.findByName(ADMIN_USERTYPE_NAME)
                .orElseGet(() -> userTypeGateway.saveNewUserType(
                        UserType.create(ADMIN_USERTYPE_NAME, Set.of(Permission.values()))));

        userGateway.findByLogin(ADMIN_LOGIN)
                .orElseGet(() -> userGateway.saveNewUser(
                        User.create(
                                adminName,
                                adminEmail,
                                ADMIN_LOGIN,
                                passwordHasher.encode(adminPassword),
                                adminUserType)));
    }

}
