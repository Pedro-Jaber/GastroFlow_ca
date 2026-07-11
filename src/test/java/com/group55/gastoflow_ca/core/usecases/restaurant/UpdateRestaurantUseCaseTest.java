package com.group55.gastoflow_ca.core.usecases.restaurant;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.restaurant.UpdateRestaurantInputDataDTO;
import com.group55.gastoflow_ca.core.entities.Restaurant;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserToken;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.enums.Permission;
import com.group55.gastoflow_ca.core.exceptions.ForbiddenActionException;
import com.group55.gastoflow_ca.core.exceptions.RestaurantNotFoundException;
import com.group55.gastoflow_ca.core.exceptions.UserNotFoundException;
import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class UpdateRestaurantUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    @Mock
    private IUserGateway userGateway;

    private UpdateRestaurantUseCase useCase;

    private UserToken requestingUserWithPermission;

    @BeforeEach
    void setup() {
        useCase = UpdateRestaurantUseCase.create(restaurantGateway, userGateway);

        UserType requesterUserType = UserType.create(
                UUID.randomUUID(), "Admin", Set.of(Permission.EDIT_RESTAURANT, Permission.EDIT_ALL_RESTAURANT));
        requestingUserWithPermission = new UserToken(
                UUID.randomUUID(), "Admin User", "admin@ex.com", "admin", requesterUserType);
    }

    @Test
    void shouldUpdateAllFieldsSuccessfully() {
        var id = UUID.randomUUID();
        var oldOwnerId = UUID.randomUUID();
        var existing = Restaurant.create(id, "Nome Antigo", "Endereço Antigo", "Brasileira", "08:00-18:00",
                oldOwnerId, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        var newOwner = User.create("Novo Dono", "novo@email.com", "novo", "123", null);
        var newOwnerId = newOwner.getId();

        var input = new UpdateRestaurantInputDataDTO("Nome Novo", "Endereço Novo", "Francesa", "09:00-23:00",
                newOwnerId);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.findById(newOwnerId)).thenReturn(Optional.of(newOwner));
        when(restaurantGateway.updateRestaurant(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getName()).isEqualTo("Nome Novo");
        assertThat(result.getAddress()).isEqualTo("Endereço Novo");
        assertThat(result.getCuisineType()).isEqualTo("Francesa");
        assertThat(result.getOpeningHours()).isEqualTo("09:00-23:00");
        assertThat(result.getOwnerId()).isEqualTo(newOwnerId);
        verify(restaurantGateway, times(1)).updateRestaurant(any(Restaurant.class));
    }

    @Test
    void shouldThrowWhenRestaurantNotFound() {
        var id = UUID.randomUUID();
        var input = new UpdateRestaurantInputDataDTO("Nome", "Endereço", "Tipo", "Horário", null);

        when(restaurantGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(restaurantGateway, never()).updateRestaurant(any());
    }

    @Test
    void shouldThrowWhenNewOwnerNotFound() {
        var id = UUID.randomUUID();
        var newOwnerId = UUID.randomUUID();
        var existing = Restaurant.create(id, "Nome", "Endereço", "Tipo", "Horário", UUID.randomUUID(),
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateRestaurantInputDataDTO(null, null, null, null, newOwnerId);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));
        when(userGateway.findById(newOwnerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.run(requestingUserWithPermission, id, input))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(newOwnerId.toString());

        verify(restaurantGateway, never()).updateRestaurant(any());
    }

    @Test
    void shouldKeepExistingFieldsWhenInputFieldsAreNullOrBlank() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var existing = Restaurant.create(id, "Nome Original", "Endereço Original", "Brasileira", "08:00-18:00",
                ownerId, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateRestaurantInputDataDTO(null, "   ", null, null, null);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.updateRestaurant(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getName()).isEqualTo("Nome Original");
        assertThat(result.getAddress()).isEqualTo("Endereço Original");
        assertThat(result.getCuisineType()).isEqualTo("Brasileira");
        assertThat(result.getOpeningHours()).isEqualTo("08:00-18:00");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        verify(userGateway, never()).findById(any());
    }

    @Test
    void shouldUpdateUpdatedAtTimestamp() {
        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var oldUpdatedAt = java.time.LocalDateTime.now().minusDays(2);
        var existing = Restaurant.create(id, "Nome", "Endereço", "Tipo", "Horário", ownerId,
                java.time.LocalDateTime.now().minusDays(3), oldUpdatedAt);
        var input = new UpdateRestaurantInputDataDTO(null, null, null, null, null);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.updateRestaurant(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.run(requestingUserWithPermission, id, input);

        assertThat(result.getUpdatedAt()).isAfter(oldUpdatedAt);
    }

    @Test
    void shouldThrowForbiddenWhenRequestingUserLacksPermission() {
        UserType requesterTypeWithoutPermission = UserType.create(UUID.randomUUID(), "Cliente", Set.of());
        UserToken requestingUserWithoutPermission = new UserToken(
                UUID.randomUUID(), "Some Client", "client@ex.com", "client", requesterTypeWithoutPermission);

        var id = UUID.randomUUID();
        var ownerId = UUID.randomUUID();
        var existing = Restaurant.create(id, "Nome", "Endereço", "Tipo", "Horário", ownerId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateRestaurantInputDataDTO("Nome Novo", null, null, null, null);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.run(requestingUserWithoutPermission, id, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(restaurantGateway, never()).updateRestaurant(any());
    }

    @Test
    void shouldAllowOwnerToUpdateOwnRestaurantWithOnlyEditRestaurantPermission() {
        UUID ownerId = UUID.randomUUID();
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.EDIT_RESTAURANT));
        UserToken ownerToken = new UserToken(ownerId, "Dono", "dono@ex.com", "dono", ownerType);

        var id = UUID.randomUUID();
        var existing = Restaurant.create(id, "Nome", "Endereço", "Tipo", "Horário", ownerId,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateRestaurantInputDataDTO("Nome Novo", null, null, null, null);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));
        when(restaurantGateway.updateRestaurant(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.run(ownerToken, id, input);

        assertThat(result.getName()).isEqualTo("Nome Novo");
    }

    @Test
    void shouldThrowForbiddenWhenUpdatingOtherRestaurantWithoutEditAllPermission() {
        UserType ownerType = UserType.create(
                UUID.randomUUID(), "Dono de Restaurante", Set.of(Permission.EDIT_RESTAURANT));
        UserToken ownerToken = new UserToken(
                UUID.randomUUID(), "Dono", "dono@ex.com", "dono", ownerType);

        var id = UUID.randomUUID();
        var existing = Restaurant.create(id, "Nome", "Endereço", "Tipo", "Horário", UUID.randomUUID(),
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        var input = new UpdateRestaurantInputDataDTO("Nome Novo", null, null, null, null);

        when(restaurantGateway.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.run(ownerToken, id, input))
                .isInstanceOf(ForbiddenActionException.class);

        verify(restaurantGateway, never()).updateRestaurant(any());
    }
}
