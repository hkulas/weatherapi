package com.example.weatherapi.service;

import com.example.weatherapi.config.WeatherApiConfig;
import com.example.weatherapi.exception.CityNotFoundException;
import com.example.weatherapi.exception.ForecastForCityAlreadyExistsException;
import com.example.weatherapi.exception.ForecastNotFoundException;
import com.example.weatherapi.exception.ForecastsNotFoundException;
import com.example.weatherapi.model.Forecast;
import com.example.weatherapi.repository.ForecastRepository;
import com.example.weatherapi.response.ForecastDto;
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

    public void fetchAndSaveForecast(String city) {
        Optional<Forecast> todayForecast = getForecastForDay(city, LocalDate.now());
        Optional<Forecast> tomorrowForecast = getForecastForDay(city, LocalDate.now().plusDays(1));


        if (todayForecast.isPresent() && tomorrowForecast.isPresent()) {
            LOGGER.info("Forecasts for the city {} for today and tomorrow already exist", city);
            throw new ForecastForCityAlreadyExistsException("Forecast for the city " + city + " for today and tomorrow already exists");
        }

        String url = buildApiUrl(city);
        WeatherResponse weatherResponse = fetchWeatherData(city, url);
        saveForecastIfNeeded(city, todayForecast, weatherResponse.getForecast().getForecastday().get(0));
        saveForecastIfNeeded(city, tomorrowForecast, weatherResponse.getForecast().getForecastday().get(1));
    }


    private WeatherResponse fetchWeatherData(String city, String url) {
        WeatherResponse weatherResponse = null;
        try {
            weatherResponse = restTemplate.getForObject(url, WeatherResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                LOGGER.warn("The City {} not found.", city);
                throw new CityNotFoundException("The City not found " + city);
            }
            throw ex;
        }

        if (weatherResponse == null || weatherResponse.getForecast() == null) {
            LOGGER.warn("Forecast for the city not found {}", city);
            throw new ForecastNotFoundException("Forecast for the city " + city + " not found");
        }
        return weatherResponse;
    }

    private void saveForecastIfNeeded(String city, Optional<Forecast> forecast, WeatherResponse.ForecastDay fetchedForecastDay) {
        if (forecast.isEmpty() && fetchedForecastDay != null) {
            saveForecast(city, fetchedForecastDay);
        }
    }

    private String buildApiUrl(String city) {
        return UriComponentsBuilder.fromHttpUrl(config.getUrl())
                .queryParam("key", config.getKey())
                .queryParam("q", city)
                .queryParam("days", config.getDays())
                .toUriString();
    }

    public Optional<Forecast> getForecastForDay(String city, LocalDate date) {
        return forecastRepository.findByCityAndDate(city, date);
    }

    public List<ForecastDto> fetchTodaysAndTomorrowsForecasts(String city) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Forecast> forecasts = new ArrayList<>();

        Optional<Forecast> todayForecast = getForecastForDay(city, today);
        Optional<Forecast> tomorrowForecast = getForecastForDay(city, tomorrow);

        todayForecast.ifPresent(forecasts::add);
        tomorrowForecast.ifPresent(forecasts::add);

        if (forecasts.isEmpty()) {
            LOGGER.warn("Forecast not found for the city {} for today and tomorrow", city);
            throw new ForecastNotFoundException("Forecast not found for the city " + city + " for today and tomorrow");
        }
        LOGGER.info("End fetching todays and tomorrows forecasts for city {}", city);
        return forecasts.stream()
                .map(this::toForecastResponse)
                .toList();
    }

    public List<ForecastDto> fetchAllForecasts() {
        LOGGER.info("Start fetching all forecasts");
        List<Forecast> forecasts = forecastRepository.findAllByOrderByCityAsc();

        if (forecasts.isEmpty()) {
            LOGGER.warn("No forecasts are found for the city");
            throw new ForecastsNotFoundException("No forecasts are available.");
        }

        LOGGER.info("End fetching all forecasts");
        return forecasts.stream()
                .map(this::toForecastResponse)
                .toList();
    }

    private void saveForecast(String city, WeatherResponse.ForecastDay forecastDay) {
        LocalDate date = forecastDay.getDate();
        LOGGER.info("Start saving forecast for city: {} and date {}", city, date);

        Optional<Forecast> existingForecast = forecastRepository.findByCityAndDate(city, date);
        if (existingForecast.isPresent()) {
            LOGGER.info("Forecast for this city {} and date {} already exists", city, forecastDay.getDate());
            return;
        }

        Forecast forecast = toForecast(city, forecastDay);

        forecastRepository.save(forecast);
        LOGGER.info("End saving forecast for city: {} and date {}", city, date);
    }

    private Forecast toForecast(String city, WeatherResponse.ForecastDay forecastDay) {
        Forecast forecast = new Forecast();
        forecast.setCity(city);
        forecast.setDate(forecastDay.getDate());
        forecast.setMaxtempC(forecastDay.getDay().getMaxtempC());
        forecast.setMintempC(forecastDay.getDay().getMintempC());
        forecast.setTotalprecipMm(forecastDay.getDay().getTotalprecipMm());
        forecast.setAvghumidity(forecastDay.getDay().getAvgHumidity());
        forecast.setCondition(forecastDay.getDay().getCondition().getText());
        return forecast;
    }
    private ForecastDto toForecastResponse(Forecast forecast) {
        ForecastDto forecastDto = new ForecastDto();
        forecastDto.setCity(forecast.getCity());
        forecastDto.setMaxTempC(Double.valueOf(forecast.getMaxtempC()));
        forecastDto.setMinTempC(Double.valueOf(forecast.getMintempC()));
        forecastDto.setTotalPrecipMm(Double.valueOf(forecast.getTotalprecipMm()));
        forecastDto.setAvgHumidity(Double.valueOf(forecast.getAvghumidity()));
        forecastDto.setCondition(forecast.getCondition());
        return forecastDto;
    }
}