package com.group55.gastoflow_ca.core.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserToken {

    private UUID userId;
    private String nome;
    private String emailAddress;
    private String login;
    private UserType userType;

    public static UserToken create(User user) {
        return new UserToken(user.getId(), user.getName(), user.getEmailAddress(), user.getLogin(), user.getUserType());
    }
}