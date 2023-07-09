package com.example.weatherapi.service;

import com.example.weatherapi.config.WeatherApiConfig;
import com.example.weatherapi.exception.CityNotFoundException;
import com.example.weatherapi.exception.ForecastForCityAlreadyExistsException;
import com.example.weatherapi.exception.ForecastNotFoundException;
import com.example.weatherapi.model.Forecast;
import com.example.weatherapi.repository.ForecastRepository;
import com.example.weatherapi.response.ForecastDto;
import com.example.weatherapi.response.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {
    private static final String TEST_CITY = "TestCity";
    private static final double HUMIDITY = 1.5;
    private static final double MAX_TEMP = 28D;
    private static final double MIN_TEMP = 13D;
    private static final double TOTAL_PRECIP = 11D;
    private static final String CONDITION = "SUNNY";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ForecastRepository forecastRepository;

    @Mock
    private WeatherApiConfig weatherApiConfig;

    @InjectMocks
    private WeatherService weatherService;

    private LocalDate today;
    private LocalDate tomorrow;
    private LocalDate twoDaysAfterToday;

    private Forecast forecastToday;
    private Forecast forecastTomorrow;
    private Forecast forecastTwoDaysAfterToday;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        tomorrow = today.plusDays(1);
        twoDaysAfterToday = today.plusDays(2);
        forecastToday = new Forecast();
        forecastToday.setCity(TEST_CITY);
        forecastToday.setDate(today);
        forecastToday.setAvghumidity(HUMIDITY);
        forecastToday.setMaxtempC(MAX_TEMP);
        forecastToday.setMintempC(MIN_TEMP);
        forecastToday.setTotalprecipMm(TOTAL_PRECIP);
        forecastToday.setCondition(CONDITION);

        forecastTomorrow = new Forecast();
        forecastTomorrow.setCity(TEST_CITY);
        forecastTomorrow.setDate(tomorrow);
        forecastTomorrow.setAvghumidity(HUMIDITY);
        forecastTomorrow.setMaxtempC(MAX_TEMP);
        forecastTomorrow.setMintempC(MIN_TEMP);
        forecastTomorrow.setTotalprecipMm(TOTAL_PRECIP);
        forecastTomorrow.setCondition(CONDITION);

        forecastTwoDaysAfterToday = new Forecast();
        forecastTwoDaysAfterToday.setCity(TEST_CITY);
        forecastTwoDaysAfterToday.setDate(twoDaysAfterToday);
        forecastTwoDaysAfterToday.setAvghumidity(HUMIDITY);
        forecastTwoDaysAfterToday.setMaxtempC(MAX_TEMP);
        forecastTwoDaysAfterToday.setMintempC(MIN_TEMP);
        forecastTwoDaysAfterToday.setTotalprecipMm(TOTAL_PRECIP);
        forecastTwoDaysAfterToday.setCondition(CONDITION);
    }

    @Test
    void fetchAndSaveForecast_savesForecasts() {
        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.of(new Forecast()));
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.of(new Forecast()));

        assertThrows(ForecastForCityAlreadyExistsException.class, () -> weatherService.fetchAndSaveForecast(TEST_CITY));

        verify(restTemplate, never()).getForObject(anyString(), eq(WeatherResponse.class));
    }

    @Test
    void fetchAndSaveForecast_SavesTwoForecastsCorrectly() {
        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.empty());
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(createWeatherResponse());

        weatherService.fetchAndSaveForecast(TEST_CITY);

        verify(forecastRepository, times(2)).save(any(Forecast.class));
    }
    @Test
    void fetchAndSaveForecast_ThrowsCityNotFoundException() {
        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.empty());
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(CityNotFoundException.class, () -> weatherService.fetchAndSaveForecast(TEST_CITY));

        verify(forecastRepository, never()).save(any(Forecast.class));
    }

    @Test
    void fetchAndSaveForecast_ThrowsForecastNotFoundException() {
        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.empty());
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(null);

        assertThrows(ForecastNotFoundException.class, () -> weatherService.fetchAndSaveForecast(TEST_CITY));

        verify(forecastRepository, never()).save(any(Forecast.class));
    }

    @Test
    void fetchTodaysAndTomorrowsForecasts_ThrowsForecastNotFoundException() {
        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.empty());
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.empty());

        assertThrows(ForecastNotFoundException.class, () -> weatherService.fetchTodaysAndTomorrowsForecasts(TEST_CITY));

        verify(restTemplate, never()).getForObject(anyString(), eq(WeatherResponse.class));
    }

    @Test
    void fetchTodaysAndTomorrowsForecasts_forecastsFound() {
        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.of(new Forecast()));
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.of(new Forecast()));


        when(forecastRepository.findByCityAndDate(TEST_CITY, today)).thenReturn(Optional.of(forecastToday));
        when(forecastRepository.findByCityAndDate(TEST_CITY, tomorrow)).thenReturn(Optional.of(forecastTomorrow));

        List<ForecastDto> result = weatherService.fetchTodaysAndTomorrowsForecasts(TEST_CITY);

        assertEquals(2, result.size());
        verify(restTemplate, never()).getForObject(anyString(), eq(WeatherResponse.class));
    }

    @Test
    void fetchAllForecasts_forecastsFound() {
        when(forecastRepository.findAll()).thenReturn(Arrays.asList(forecastToday, forecastTomorrow, forecastTwoDaysAfterToday));
        List<ForecastDto> result = weatherService.fetchAllForecasts();
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }
    private WeatherResponse createWeatherResponse() {
        WeatherResponse response = new WeatherResponse();

        WeatherResponse.Location location = new WeatherResponse.Location();
        location.setName(TEST_CITY);
        response.setLocation(location);

        response.setForecast(createWeatherForecast());

        return response;
    }

    private WeatherResponse.Forecast createWeatherForecast() {
        WeatherResponse.Forecast forecast = new WeatherResponse.Forecast();
        forecast.setForecastday(Arrays.asList(createForecastDay1(), createForecastDay2()));

        return forecast;
    }

    private WeatherResponse.ForecastDay createForecastDay1() {
        return createForecastDay(LocalDate.now(), HUMIDITY, MAX_TEMP, MIN_TEMP, TOTAL_PRECIP, CONDITION);
    }

    private WeatherResponse.ForecastDay createForecastDay2() {
        return createForecastDay(LocalDate.now().plusDays(1), HUMIDITY, MAX_TEMP, MIN_TEMP, TOTAL_PRECIP, CONDITION);
    }

    private WeatherResponse.ForecastDay createForecastDay(LocalDate date, double humidity, double maxTemp, double minTemp, double totalPrecip, String conditionText) {
        WeatherResponse.ForecastDay forecastDay = new WeatherResponse.ForecastDay();
        forecastDay.setDate(date);

        WeatherResponse.Day day = new WeatherResponse.Day();
        day.setAvghumidity(humidity);
        day.setMaxtemp_c(maxTemp);
        day.setMintemp_c(minTemp);
        day.setTotalprecip_mm(totalPrecip);

        WeatherResponse.Condition condition = new WeatherResponse.Condition();
        condition.setText(conditionText);
        day.setCondition(condition);

        forecastDay.setDay(day);

        return forecastDay;
    }
}
