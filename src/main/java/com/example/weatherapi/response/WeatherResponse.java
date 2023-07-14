package com.example.weatherapi.response;

import com.example.weatherapi.exception.CustomError;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class WeatherResponse {
    private Location location;
    private Forecast forecast;

    private CustomError customError;
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public CustomError getCustomError() {
        return customError;
    }

    public void setCustomError(CustomError customError) {
        this.customError = customError;
    }

    public static class Location {
        private String name;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Forecast {
        private List<ForecastDay> forecastday;

        public List<ForecastDay> getForecastday() {
            return forecastday;
        }

        public void setForecastday(List<ForecastDay> forecastday) {
            this.forecastday = forecastday;
        }
    }

    public static class ForecastDay {
        private LocalDate date;
        private Day day;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Day getDay() {
            return day;
        }

        public void setDay(Day day) {
            this.day = day;
        }
    }

    public static class Day {
        @JsonProperty("maxtemp_c")
        private double maxtempC;
        @JsonProperty("mintemp_c")
        private double mintempC;
        @JsonProperty("totalprecip_mm")
        private double totalprecipMm;
        @JsonProperty("avghumidity")
        private double avgHumidity;
        private Condition condition;

        public double getMaxtempC() {
            return maxtempC;
        }

        public void setMaxtempC(double maxtempC) {
            this.maxtempC = maxtempC;
        }

        public double getMintempC() {
            return mintempC;
        }

        public void setMintempC(double mintempC) {
            this.mintempC = mintempC;
        }

        public double getTotalprecipMm() {
            return totalprecipMm;
        }

        public void setTotalprecipMm(double totalprecipMm) {
            this.totalprecipMm = totalprecipMm;
        }

        public double getAvgHumidity() {
            return avgHumidity;
        }

        public void setAvgHumidity(double avgHumidity) {
            this.avgHumidity = avgHumidity;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    public static class Condition {
        private String text;
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}