package com.group55.gastoflow_ca.core.interfaces.auth;

import com.group55.gastoflow_ca.core.entities.UserToken;

public interface ITokenProvider {
    String generateToken(UserToken userToken);

    UserToken parseToken(String token);
}
