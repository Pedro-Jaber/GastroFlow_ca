package com.group55.gastoflow_ca.core.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class User {

    private UUID id;
    private String name;
    private String emailAddress;
    private String login;
    private String password;
    private UserType userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User create(
            String name,
            String emailAddress,
            String login,
            String password,
            UserType userType) {

        User user = new User();

        user.id = UUID.randomUUID();
        user.name = name;
        user.emailAddress = emailAddress;
        user.login = login;
        user.password = password;
        user.userType = userType;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

    public static User create(
            UUID id,
            String name,
            String emailAddress,
            String login,
            String password,
            UserType userType) {

        User user = new User();

        user.id = id;
        user.name = name;
        user.emailAddress = emailAddress;
        user.login = login;
        user.password = password;
        user.userType = userType;
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        return user;
    }

}
