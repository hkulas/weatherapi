package com.example.weatherapi.service;

import com.example.weatherapi.config.WeatherApiConfig;
import com.example.weatherapi.exception.CityNotFoundException;
import com.example.weatherapi.exception.ForecastForCityAlreadyExistsException;
import com.example.weatherapi.exception.ForecastNotFoundException;
import com.example.weatherapi.exception.ForecastsNotFoundException;
import com.example.weatherapi.model.Forecast;
import com.example.weatherapi.repository.ForecastRepository;
import com.example.weatherapi.response.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    private RestTemplate restTemplate;
    private ForecastRepository forecastRepository;
    private WeatherApiConfig config;

    public WeatherService(RestTemplate restTemplate, ForecastRepository forecastRepository, WeatherApiConfig config) {
        this.restTemplate = restTemplate;
        this.forecastRepository = forecastRepository;
        this.config = config;
    }

    public Optional<WeatherResponse> fetchAndSaveForecast(String city) {
        Optional<Forecast> todayForecast = getForecastForDay(city, LocalDate.now());
        Optional<Forecast> tomorrowForecast = getForecastForDay(city, LocalDate.now().plusDays(1));

        if (todayForecast.isPresent() && tomorrowForecast.isPresent()) {
            LOGGER.info("Forecasts for city {} for today and tomorrow already exist", city);
            throw new ForecastForCityAlreadyExistsException("Forecast for city " + city + " for today and tomorrow already exists");
        }

        return fetchFromApiAndSave(city, todayForecast, tomorrowForecast);
    }


    private Optional<WeatherResponse> fetchFromApiAndSave(String city, Optional<Forecast> todayForecast, Optional<Forecast> tomorrowForecast) {
        String url = UriComponentsBuilder.fromHttpUrl("http://api.weatherapi.com/v1/forecast.json")
                .queryParam("key", config.getKey())
                .queryParam("q", city)
                .queryParam("days", config.getDays())
                .toUriString();

        try {
            WeatherResponse weatherResponse = restTemplate.getForObject(url, WeatherResponse.class);
            if (weatherResponse == null) {
                LOGGER.error("Forecast for city not found {}", city);
                throw new ForecastNotFoundException("Forecast for city " + city + " not found");
            }

            WeatherResponse.ForecastDay fetchedToday = weatherResponse.getForecast().getForecastday().get(0);
            WeatherResponse.ForecastDay fetchedTomorrow = weatherResponse.getForecast().getForecastday().get(1);

            if (todayForecast.isEmpty()) {
                saveForecast(city, fetchedToday);
            }

            if (tomorrowForecast.isEmpty()) {
                saveForecast(city, fetchedTomorrow);
            }

            return Optional.of(weatherResponse);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                LOGGER.error("City {} not found.", city);
                throw new CityNotFoundException("City not found " + city);
            }
            throw ex;
        }
    }

    private Optional<Forecast> getForecastForDay(String city, LocalDate date) {
        Optional<Forecast> forecast = forecastRepository.findByCityAndDate(city, date);
        forecast.ifPresent(f -> LOGGER.info("Forecast for city {} and date {} already exists", city, date));
        return forecast;
    }

    public Optional<List<Forecast>> fetchTodaysAndTomorrowsForecast(String city) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Forecast> forecasts = new ArrayList<>();

        Optional<Forecast> todayForecast = getOrFetchForecast(city, today);
        Optional<Forecast> tomorrowForecast = getOrFetchForecast(city, tomorrow);

        todayForecast.ifPresent(forecasts::add);
        tomorrowForecast.ifPresent(forecasts::add);

        if (forecasts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(forecasts);
    }

    public List<Forecast> fetchAllForecasts() {
        List<Forecast> forecasts = forecastRepository.findAll();

        if (forecasts.isEmpty()) {
            throw new ForecastsNotFoundException("No forecasts are available.");
        }

        forecasts.sort(Comparator.comparing(Forecast::getCity));

        return forecasts;
    }

    private void saveForecast(String city, WeatherResponse.ForecastDay forecastDay) {
        LocalDate date = forecastDay.getDate();
        LOGGER.info("Start fetching and saving forecast for city: {} and date {}", city, date);

        Optional<Forecast> existingForecast = forecastRepository.findByCityAndDate(city, date);
        if (existingForecast.isPresent()) {
            LOGGER.info("Forecast for this city {} and date {} already exists", city, forecastDay.getDate());
            return;
        }

        Forecast forecast = toForecast(city, forecastDay);

        forecastRepository.save(forecast);
        LOGGER.info("End fetching and saving forecast for city: {} and date {}", city, date);
    }

    private Optional<Forecast> getOrFetchForecast(String city, LocalDate date) {
        Optional<Forecast> forecast = forecastRepository.findByCityAndDate(city, date);

        if (!forecast.isPresent()) {
            Optional<WeatherResponse> response = fetchAndSaveForecast(city);
            if (response.isPresent()) {
                forecast = forecastRepository.findByCityAndDate(city, date);
            }
        }

        return forecast;
    }

    private Forecast toForecast(String city, WeatherResponse.ForecastDay forecastDay) {
        Forecast forecast = new Forecast();
        forecast.setCity(city);
        forecast.setDate(forecastDay.getDate());
        forecast.setMaxtempC(forecastDay.getDay().getMaxtemp_c());
        forecast.setMintempC(forecastDay.getDay().getMintemp_c());
        forecast.setTotalprecipMm(forecastDay.getDay().getTotalprecip_mm());
        forecast.setAvghumidity(forecastDay.getDay().getAvghumidity());
        forecast.setConditionText(forecastDay.getDay().getCondition().getText());
        return forecast;
    }
}