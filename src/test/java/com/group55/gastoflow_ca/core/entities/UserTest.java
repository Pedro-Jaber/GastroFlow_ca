package com.group55.gastoflow_ca.core.entities;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @DisplayName("Test User Creation")
    @Test
    void testUserCreation() {

        // Arrange
        String name = "John Doe";
        String emailAddress = "jdoe@ex.com";
        String login = "jdoe";
        String password = "password123";
        UserType userType = UserType.create(UUID.randomUUID(), "Admin", null);

        // Act
        User user = User.create(name, emailAddress, login, password, userType);

        // Assert
        assert user != null;
        assertEquals(name, user.getName());
        assertEquals(emailAddress, user.getEmailAddress());
        assertEquals(login, user.getLogin());
        assertEquals(password, user.getPassword());
        assertEquals(userType, user.getUserType());
    }

    @DisplayName("Test User Creation with ID")
    @Test
    void testUserCreationWithId() {

        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Jane Doe";
        String emailAddress = "jdoe@ex.com";
        String login = "jdoe";
        String password = "password123";
        UserType userType = UserType.create(UUID.randomUUID(), "Admin", null);

        // Act
        User user = User.create(id, name, emailAddress, login, password, userType);

        // Assert
        assert user != null;
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(emailAddress, user.getEmailAddress());
        assertEquals(login, user.getLogin());
        assertEquals(password, user.getPassword());
        assertEquals(userType, user.getUserType());
    }

}
