package com.group55.gastoflow_ca.core.exceptions;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(String message) {
        super(message);
    }

}
