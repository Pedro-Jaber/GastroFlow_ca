package com.group55.gastoflow_ca.core.presenters;

import com.group55.gastoflow_ca.core.dtos.user.UserOutputDTO;
import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeDTO;
import com.group55.gastoflow_ca.core.entities.User;

public class UserPresenter {
    public static UserOutputDTO toOutputDTO(User user) {
        UserOutputDTO userOutDTO = new UserOutputDTO(
                user.getId(),
                user.getName(),
                user.getEmailAddress(),
                user.getLogin(),
                new UserTypeDTO(
                        user.getUserType().getId(),
                        user.getUserType().getName(),
                        user.getUserType().getPermissions()),
                user.getCreatedAt(),
                user.getUpdatedAt());

        return userOutDTO;
    }
}
