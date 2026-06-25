package com.group55.gastoflow_ca.core.presenters;

import com.group55.gastoflow_ca.core.dtos.usertype.UserTypeOutputDTO;
import com.group55.gastoflow_ca.core.entities.UserType;

public class UserTypePresenter {

    public static UserTypeOutputDTO toOutputDTO(UserType userType) {
        UserTypeOutputDTO userTypeOutputDTO = new UserTypeOutputDTO(
                userType.getId(),
                userType.getName(),
                userType.getPermissions());

        return userTypeOutputDTO;
    }

}
