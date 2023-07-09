package com.example.weatherapi.controller;

import com.example.weatherapi.response.ForecastDto;
import com.example.weatherapi.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {
    private static final String URL = "/api/weather/forecast/";

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private MockMvc mockMvc;

    private String city;
    private ForecastDto forecast1;
    private ForecastDto forecast2;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
        city = "Test City";
        forecast1 = new ForecastDto();
        forecast2 = new ForecastDto();
    }

    @Test
    public void testSaveForecast() throws Exception {
        String city = "Test City";
        mockMvc.perform(post(URL + city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testFetchTodaysAndTomorrowsForecast() throws Exception {
        List<ForecastDto> forecasts = Arrays.asList(forecast1, forecast2);
        when(weatherService.fetchTodaysAndTomorrowsForecasts(city)).thenReturn(forecasts);

        mockMvc.perform(get(URL + city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}, {}]"));
    }

    @Test
    public void testFetchAllForecasts() throws Exception {
        List<ForecastDto> forecasts = Arrays.asList(forecast1, forecast2);
        when(weatherService.fetchAllForecasts()).thenReturn(forecasts);

        mockMvc.perform(get("/api/weather/forecast")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}, {}]"));
    }
}