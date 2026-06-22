package com.group55.gastoflow_ca.core.usecases;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.group55.gastoflow_ca.core.dtos.user.CreateUserInputDataDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;
import com.group55.gastoflow_ca.core.usecases.user.CreateUserUseCase;

public class CreateUserUseCaseTest {

    @DisplayName("Test Create User Use Case")
    @Test
    void testCreateUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String name = "John Doe";
        String emailAddress = "jdoe@ex.com";
        String login = "jdoe";
        String password = "password123";
        UserType userType = UserType.create(UUID.randomUUID(), "Admin", null);

        IUserGateway userGateway = mock(IUserGateway.class);
        when(userGateway.findByLogin(anyString())).thenReturn(null);
        when(userGateway.saveNewUser(any()))
                .thenReturn(
                        User.create(userId, name, emailAddress, login, password, userType));

        IUserTypeGateway userTypeGateway = mock(IUserTypeGateway.class);
        when(userTypeGateway.findById(any())).thenReturn(userType);

        // Act
        User user = CreateUserUseCase.create(userGateway, userTypeGateway)
                .run(new CreateUserInputDataDTO(name, emailAddress, login, password, userType.getId()));

        // Assert
        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(emailAddress, user.getEmailAddress());
        assertEquals(login, user.getLogin());
        assertEquals(password, user.getPassword());
        assertEquals(userType, user.getUserType());
    }
}
