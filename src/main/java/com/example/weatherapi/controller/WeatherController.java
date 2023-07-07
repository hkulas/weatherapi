package com.example.weatherapi.controller;

import com.example.weatherapi.constant.ErrorCode;
import com.example.weatherapi.exception.CityNotFoundException;
import com.example.weatherapi.exception.CustomError;
import com.example.weatherapi.exception.ForecastForCityAlreadyExistsException;
import com.example.weatherapi.exception.ForecastsNotFoundException;
import com.example.weatherapi.model.Forecast;
import com.example.weatherapi.response.WeatherResponse;
import com.example.weatherapi.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather/forecast")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/{city}")
    public ResponseEntity<WeatherResponse> saveForecast(@PathVariable String city) {
        try {
            Optional<WeatherResponse> savedWeatherResponse = weatherService.fetchAndSaveForecast(city);
            return ResponseEntity.ok(savedWeatherResponse.get());
        } catch (CityNotFoundException ex) {
            WeatherResponse response = new WeatherResponse();
            response.setCustomError(new CustomError(ErrorCode.CITY_NOT_FOUND, ex.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (ForecastForCityAlreadyExistsException ex) {
            WeatherResponse response = new WeatherResponse();
            response.setCustomError(new CustomError(ErrorCode.FORECAST_FOR_CITY_ALREADY_EXISTS, ex.getMessage()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }


    @GetMapping("/{city}")
    public ResponseEntity<?> fetchTodaysAndTomorrowsForecast(@PathVariable String city) {
        Optional<List<Forecast>> fetchedForecasts = weatherService.fetchTodaysAndTomorrowsForecast(city);

        if (fetchedForecasts.isEmpty() || fetchedForecasts.get().isEmpty()) {
            WeatherResponse response = new WeatherResponse();
            response.setCustomError(new CustomError(ErrorCode.FORECAST_NOT_FOUND, "Forecast not found for the city on the required dates"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            return ResponseEntity.ok(fetchedForecasts.get());
        }
    }

    @GetMapping()
    public ResponseEntity<?> fetchAllForecasts() {
        try {
            List<Forecast> forecasts = weatherService.fetchAllForecasts();
            return ResponseEntity.ok(forecasts);
        } catch (ForecastsNotFoundException ex) {
            CustomError customError = new CustomError(ErrorCode.FORECASTS_NOT_FOUND, "No forecasts are available.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customError);
        }
    }
}

