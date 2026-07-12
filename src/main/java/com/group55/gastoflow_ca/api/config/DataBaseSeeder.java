package com.group55.gastoflow_ca.api.config;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.core.entities.MenuItem;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.gateways.MenuItemGateway;
import com.group55.gastoflow_ca.core.gateways.RestaurantGateway;
import com.group55.gastoflow_ca.core.gateways.UserGateway;
import com.group55.gastoflow_ca.core.gateways.UserTypeGateway;
import com.group55.gastoflow_ca.core.interfaces.auth.IPasswordHasher;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IMenuItemDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserTypeDataSource;
import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

@Component
public class DataBaseSeeder implements CommandLineRunner {

        private static final String ADMIN_USERTYPE_NAME = "Admin";
        private static final String CLIENTE_USERTYPE_NAME = "Cliente";
        private static final String RESTAURANT_OWNER_USERTYPE_NAME = "RestaurantOwner";

        private static final String ADMIN_LOGIN = "admin";
        private static final String CLIENTE_LOGIN = "cliente";
        private static final String RESTAURANT_OWNER_LOGIN = "restowner";

        private static final String SEED_RESTAURANT_NAME = "Sabor Caseiro";
        private static final String DEFAULT_SEED_PASSWORD = "senha123";

        private final IUserTypeDataSource userTypeDataSource;
        private final IUserDataSource userDataSource;
        private final IRestaurantDataSource restaurantDataSource;
        private final IMenuItemDataSource menuItemDataSource;
        private final IPasswordHasher passwordHasher;

        private final String adminName;
        private final String adminEmail;
        private final String adminPassword;

        public DataBaseSeeder(
                        IUserTypeDataSource userTypeDataSource,
                        IUserDataSource userDataSource,
                        IRestaurantDataSource restaurantDataSource,
                        IMenuItemDataSource menuItemDataSource,
                        IPasswordHasher passwordHasher,
                        @Value("${admin.name:Administrador}") String adminName,
                        @Value("${admin.email:admin@gastoflow.com}") String adminEmail,
                        @Value("${admin.password:admin123}") String adminPassword) {
                this.userTypeDataSource = userTypeDataSource;
                this.userDataSource = userDataSource;
                this.restaurantDataSource = restaurantDataSource;
                this.menuItemDataSource = menuItemDataSource;
                this.passwordHasher = passwordHasher;
                this.adminName = adminName;
                this.adminEmail = adminEmail;
                this.adminPassword = adminPassword;
        }

        @Override
        public void run(String... args) {
                UserTypeGateway userTypeGateway = UserTypeGateway.create(userTypeDataSource);
                UserGateway userGateway = UserGateway.create(userDataSource);
                RestaurantGateway restaurantGateway = RestaurantGateway.create(restaurantDataSource);
                IMenuItemGateway menuItemGateway = MenuItemGateway.create(menuItemDataSource);

                UserType adminUserType = seedUserType(userTypeGateway, ADMIN_USERTYPE_NAME,
                                Set.of(Permission.values()));

                UserType clienteUserType = seedUserType(userTypeGateway, CLIENTE_USERTYPE_NAME, Set.of(
                                Permission.READ_USER,
                                Permission.EDIT_USER,
                                Permission.DELETE_USER,
                                Permission.READ_RESTAURANT,
                                Permission.READ_ALL_RESTAURANT,
                                Permission.READ_MENU_ITEM,
                                Permission.READ_ALL_MENU_ITEM));

                UserType restaurantOwnerUserType = seedUserType(userTypeGateway, RESTAURANT_OWNER_USERTYPE_NAME, Set.of(
                                Permission.READ_USER,
                                Permission.EDIT_USER,
                                Permission.DELETE_USER,
                                Permission.CREATE_RESTAURANT,
                                Permission.READ_RESTAURANT,
                                Permission.READ_ALL_RESTAURANT,
                                Permission.EDIT_RESTAURANT,
                                Permission.DELETE_RESTAURANT,
                                Permission.CREATE_MENU_ITEM,
                                Permission.READ_MENU_ITEM,
                                Permission.READ_ALL_MENU_ITEM,
                                Permission.EDIT_MENU_ITEM,
                                Permission.DELETE_MENU_ITEM));

                seedUser(userGateway, ADMIN_LOGIN, adminName, adminEmail, adminPassword, adminUserType);

                seedUser(userGateway, CLIENTE_LOGIN, "Cliente Exemplo", "cliente@gastoflow.com",
                                DEFAULT_SEED_PASSWORD, clienteUserType);

                User restaurantOwner = seedUser(userGateway, RESTAURANT_OWNER_LOGIN, "Dono do Restaurante",
                                "restowner@gastoflow.com", DEFAULT_SEED_PASSWORD, restaurantOwnerUserType);

                boolean restaurantAlreadyExisted = restaurantGateway.findByName(SEED_RESTAURANT_NAME).isPresent();

                Restaurant restaurant = restaurantGateway.findByName(SEED_RESTAURANT_NAME)
                                .orElseGet(() -> restaurantGateway.saveNewRestaurant(
                                                Restaurant.create(
                                                                SEED_RESTAURANT_NAME,
                                                                "Rua Coronel Jailson Mendes, 123",
                                                                "Brasileira",
                                                                "08:00 - 22:00",
                                                                restaurantOwner.getId())));

                if (!restaurantAlreadyExisted) {
                        menuItemGateway.saveNewMenuItem(MenuItem.create(
                                        "Feijoada Completa",
                                        "Feijoada tradicional com arroz, couve e farofa",
                                        new BigDecimal("39.90"),
                                        false,
                                        "path/to/feijoada.jpg",
                                        restaurant.getId()));

                        menuItemGateway.saveNewMenuItem(MenuItem.create(
                                        "Suco Natural de Laranja",
                                        "Suco natural de laranja, 500ml",
                                        new BigDecimal("8.50"),
                                        true,
                                        "path/to/suco.jpg",
                                        restaurant.getId()));
                }
        }

        private UserType seedUserType(UserTypeGateway userTypeGateway, String name, Set<Permission> permissions) {
                return userTypeGateway.findByName(name)
                                .orElseGet(() -> userTypeGateway.saveNewUserType(UserType.create(name, permissions)));
        }

        private User seedUser(UserGateway userGateway, String login, String name, String email, String rawPassword,
                        UserType userType) {
                return userGateway.findByLogin(login)
                                .orElseGet(() -> userGateway.saveNewUser(
                                                User.create(name, email, login, passwordHasher.encode(rawPassword),
                                                                userType)));
        }

}
