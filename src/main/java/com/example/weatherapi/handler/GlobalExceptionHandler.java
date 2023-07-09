package com.example.weatherapi.handler;

import com.example.weatherapi.constant.ErrorCode;
import com.example.weatherapi.exception.CityNotFoundException;
import com.example.weatherapi.exception.ForecastForCityAlreadyExistsException;
import com.example.weatherapi.exception.ForecastNotFoundException;
import com.example.weatherapi.exception.ForecastsNotFoundException;
import com.example.weatherapi.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFoundException(CityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorCode.CITY_NOT_FOUND);
        errorResponse.setDescription(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForecastForCityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFoundException(ForecastForCityAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorCode.FORECAST_FOR_CITY_ALREADY_EXISTS);
        errorResponse.setDescription(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForecastNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleForecastNotFound(ForecastNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorCode.FORECAST_NOT_FOUND);
        errorResponse.setDescription(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForecastsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleForecastsNotFound(ForecastsNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ErrorCode.FORECASTS_NOT_FOUND);
        errorResponse.setDescription(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
