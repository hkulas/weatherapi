package com.example.weatherapi.controller;

import com.example.weatherapi.response.ForecastDto;
import com.example.weatherapi.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather/forecast")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/{city}")
    public ResponseEntity<Void> saveForecast(@PathVariable String city) {
        weatherService.fetchAndSaveForecast(city);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/{city}")
    public ResponseEntity<List<ForecastDto>> fetchTodaysAndTomorrowsForecast(@PathVariable String city) {
        List<ForecastDto> fetchedForecasts = weatherService.fetchTodaysAndTomorrowsForecasts(city);
        return new ResponseEntity<>(fetchedForecasts, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<ForecastDto>> fetchAllForecasts() {
        List<ForecastDto> fetchedAllForecasts = weatherService.fetchAllForecasts();
        return new ResponseEntity<>(fetchedAllForecasts, HttpStatus.OK);

    }
}

