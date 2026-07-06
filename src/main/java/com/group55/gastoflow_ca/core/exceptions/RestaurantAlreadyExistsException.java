package com.group55.gastoflow_ca.core.exceptions;

public class RestaurantAlreadyExistsException extends RuntimeException {

    public RestaurantAlreadyExistsException(String message) {
        super(message);
    }
}
