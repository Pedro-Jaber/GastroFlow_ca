package com.group55.gastoflow_ca.core.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.restaurant.CreateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantOutputDTO;
import com.group55.gastoflow_ca.core.dtos.restaurant.UpdateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.dtos.user.UserDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IUserDataSource;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private IRestaurantDataSource restaurantDataSource;

    @Mock
    private IUserDataSource userDataSource;

    private RestaurantController controller;

    private UserToken adminToken;

    @BeforeEach
    void setup() {
        controller = RestaurantController.create(restaurantDataSource, userDataSource);

        UserType adminUserType = UserType.create(UUID.randomUUID(), "Admin", Set.of(Permission.values()));
        adminToken = new UserToken(UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", adminUserType);
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        UUID ownerId = UUID.randomUUID();
        UserTypeDTO userTypeDTO = new UserTypeDTO(UUID.randomUUID(), "RestaurantOwner", Set.of());
        UserDTO ownerDTO = new UserDTO(ownerId, "Owner", "owner@ex.com", "owner", "encoded",
                userTypeDTO, LocalDateTime.now(), LocalDateTime.now());

        when(restaurantDataSource.findByName("Sabor Caseiro")).thenReturn(Optional.empty());
        when(userDataSource.findById(ownerId)).thenReturn(Optional.of(ownerDTO));
        when(restaurantDataSource.saveNewRestaurant(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new CreateRestaurantInputDataDTO("Sabor Caseiro", "Rua das Flores, 123", "Brasileira",
                "08:00 - 22:00", ownerId);

        RestaurantOutputDTO output = controller.createRestaurant(adminToken, input);

        assertThat(output.name()).isEqualTo("Sabor Caseiro");
        assertThat(output.ownerId()).isEqualTo(ownerId);
        verify(restaurantDataSource, times(1)).saveNewRestaurant(any());
    }

    @Test
    void shouldThrowForbiddenWhenCreatingRestaurantWithoutPermission() {
        UserType noPermissionType = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken tokenWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", noPermissionType);

        var input = new CreateRestaurantInputDataDTO("Sabor Caseiro", "Rua das Flores, 123", "Brasileira",
                "08:00 - 22:00", UUID.randomUUID());

        assertThatThrownBy(() -> controller.createRestaurant(tokenWithoutPermission, input))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void shouldReturnPageOfRestaurants() {
        var restaurantDTO = new RestaurantDTO(UUID.randomUUID(), "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        var pageInput = new PageInputDTO(0, 10);
        when(restaurantDataSource.findAll(pageInput))
                .thenReturn(new PageOutputDTO<>(List.of(restaurantDTO), 0, 10, 1L, 1));

        PageOutputDTO<RestaurantOutputDTO> result = controller.getAllRestaurants(adminToken, pageInput);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Sabor Caseiro");
    }

    @Test
    void shouldReturnRestaurantById() {
        UUID id = UUID.randomUUID();
        var restaurantDTO = new RestaurantDTO(id, "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(restaurantDataSource.findById(id)).thenReturn(Optional.of(restaurantDTO));

        RestaurantOutputDTO result = controller.getRestaurantById(adminToken, id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Sabor Caseiro");
    }

    @Test
    void shouldUpdateRestaurantSuccessfully() {
        UUID id = UUID.randomUUID();
        var existingDTO = new RestaurantDTO(id, "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(restaurantDataSource.findById(id)).thenReturn(Optional.of(existingDTO));
        when(restaurantDataSource.updateRestaurant(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var input = new UpdateRestaurantInputDataDTO("Novo Nome", null, null, null, null);
        RestaurantOutputDTO result = controller.updateRestaurant(adminToken, id, input);

        assertThat(result.name()).isEqualTo("Novo Nome");
        verify(restaurantDataSource, times(1)).updateRestaurant(any());
    }

    @Test
    void shouldDeleteRestaurant() {
        UUID id = UUID.randomUUID();
        var existingDTO = new RestaurantDTO(id, "Sabor Caseiro", "Rua das Flores, 123",
                "Brasileira", "08:00 - 22:00", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now());

        when(restaurantDataSource.findById(id)).thenReturn(Optional.of(existingDTO));

        controller.deleteRestaurant(adminToken, id);

        verify(restaurantDataSource, times(1)).deleteById(id);
    }
}
