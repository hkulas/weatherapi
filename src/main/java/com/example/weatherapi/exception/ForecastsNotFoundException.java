package com.example.weatherapi.exception;

public class ForecastsNotFoundException extends RuntimeException {
    public ForecastsNotFoundException(String message) {
        super(message);
    }
}

