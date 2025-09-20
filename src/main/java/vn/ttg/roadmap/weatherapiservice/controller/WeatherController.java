package vn.ttg.roadmap.weatherapiservice.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ttg.roadmap.weatherapiservice.dto.CurrentWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.HistoricalWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherForecastRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;
import vn.ttg.roadmap.weatherapiservice.service.WeatherApiException;
import vn.ttg.roadmap.weatherapiservice.service.WeatherService;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    private WeatherService weatherService;

    /**
     * Get current weather for a specific location
     * @param location Location name (city, country or coordinates)
     * @return WeatherResponse containing current weather data
     */
    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> getCurrentWeather(@RequestParam String location) {
        
        LOGGER.info("Received request for current weather: {}", location);
        
        // Create request object for validation
        CurrentWeatherRequest request = new CurrentWeatherRequest(location);
        
        // Validate the request
        validateCurrentWeatherRequest(request);
        
        WeatherResponse response = weatherService.getCurrentWeather(location);
        LOGGER.info("Successfully retrieved current weather for: {}", location);
        return ResponseEntity.ok(response);
    }

    /**
     * Get weather forecast for a specific location and date range
     * @param location Location name (city, country or coordinates)
     * @param startDate Start date in yyyy-MM-dd format
     * @param endDate End date in yyyy-MM-dd format
     * @return WeatherResponse containing forecast data
     */
    @GetMapping("/forecast")
    public ResponseEntity<WeatherResponse> getForecast(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        LOGGER.info("Received request for forecast: {} from {} to {}", location, startDate, endDate);
        
        // Create request object for validation
        WeatherForecastRequest request = new WeatherForecastRequest(location, startDate, endDate);
        
        // Validate the request
        validateForecastRequest(request);

        WeatherResponse response = weatherService.getForecast(location, startDate, endDate);
        LOGGER.info("Successfully retrieved forecast for: {} from {} to {}", location, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Get historical weather data for a specific location and date range
     * @param location Location name (city, country or coordinates)
     * @param startDate Start date in yyyy-MM-dd format
     * @param endDate End date in yyyy-MM-dd format
     * @return WeatherResponse containing historical weather data
     */
    @GetMapping("/historical")
    public ResponseEntity<WeatherResponse> getHistoricalWeather(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        LOGGER.info("Received request for historical weather: {} from {} to {}", location, startDate, endDate);
        
        // Create request object for validation
        HistoricalWeatherRequest request = new HistoricalWeatherRequest(location, startDate, endDate);
        
        // Validate the request
        validateHistoricalWeatherRequest(request);

        WeatherResponse response = weatherService.getHistorical(location, startDate, endDate);
        LOGGER.info("Successfully retrieved historical weather for: {} from {} to {}",
                   location, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Validates CurrentWeatherRequest
     */
    private void validateCurrentWeatherRequest(CurrentWeatherRequest request) {
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new WeatherApiException("Location cannot be null or empty", null);
        }
        if (request.getLocation().length() < 2) {
            throw new WeatherApiException("Location must be at least 2 characters long", null);
        }
    }

    /**
     * Validates WeatherForecastRequest
     */
    private void validateForecastRequest(WeatherForecastRequest request) {
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new WeatherApiException("Location cannot be null or empty", null);
        }
        if (request.getLocation().length() < 2) {
            throw new WeatherApiException("Location must be at least 2 characters long", null);
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new WeatherApiException("Both start and end dates are required", null);
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new WeatherApiException("Start date cannot be after end date", null);
        }
        if (request.getStartDate().isAfter(LocalDate.now().plusDays(15))) {
            throw new WeatherApiException("Start date cannot be more than 15 days in the future", null);
        }
    }

    /**
     * Validates HistoricalWeatherRequest
     */
    private void validateHistoricalWeatherRequest(HistoricalWeatherRequest request) {
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new WeatherApiException("Location cannot be null or empty", null);
        }
        if (request.getLocation().length() < 2) {
            throw new WeatherApiException("Location must be at least 2 characters long", null);
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new WeatherApiException("Both start and end dates are required", null);
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new WeatherApiException("Start date cannot be after end date", null);
        }
        if (request.getStartDate().isAfter(LocalDate.now())) {
            throw new WeatherApiException("Historical data cannot include future dates", null);
        }
        if (request.getEndDate().isAfter(LocalDate.now())) {
            throw new WeatherApiException("Historical data cannot include future dates", null);
        }
    }
}