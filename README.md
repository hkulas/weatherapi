# Weather Forecast API

This application is a Spring Boot application that allows users to save and retrieve weather forecasts.

## Requirements

- Java 20
- Maven

## Run Locally

To run the application, you need to follow these steps:

1. Clone the repository
\```bash
git clone https://github.com/username/repository.git
\```

2. Navigate into the directory
\```bash
cd repository
\```

3. Build the project with Maven
\```bash
mvn clean install
\```

4. Run the application
\```bash
mvn spring-boot:run
\```

## API Usage

### Save Forecast

- Description: Saves forecasts for today and tomorrow for chosen city.
- Request: `POST /api/weather/forecast/{city}`
- Response: HTTP Status 201 Created on success, 404 Not Found if city not found, 409 Conflict if forecast for the city already exists.

### Get Forecasts for City

- Description: Get a list of forecasts for today and tomorrow for chosen city.
- Request: `GET /api/weather/forecast/{city}`
- Response: JSON array of forecast data. HTTP Status 200 on success, 404 Not Found if no forecast found.

### Get All Forecasts

- Description: Get a list of all available forecasts.
- Request: `GET /api/weather/forecast`
- Response: JSON array of all forecasts. HTTP Status 200 on success, 404 Not Found if no forecasts found.
