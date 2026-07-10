package com.group55.gastoflow_ca.core.interfaces.auth;

public interface IPasswordHasher {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
