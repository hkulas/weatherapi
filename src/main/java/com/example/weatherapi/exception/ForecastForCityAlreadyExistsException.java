package com.example.weatherapi.exception;

public class ForecastForCityAlreadyExistsException extends RuntimeException {
    public ForecastForCityAlreadyExistsException(String message) {
        super(message);
    }
}
