package vn.ttg.roadmap.weatherapiservice.controller.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ttg.roadmap.weatherapiservice.controller.WeatherServiceApi;
import vn.ttg.roadmap.weatherapiservice.dto.CurrentWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.HistoricalWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherForecastRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherApiException;
import vn.ttg.roadmap.weatherapiservice.service.WeatherService;

/**
 * Controller implementation the WeatherServiceApi.
 *
 * @author ttg
 */
@RestController
public class WeatherServiceApiController implements WeatherServiceApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServiceApiController.class);

    @Autowired
    private WeatherService weatherService;

    @Override
    public ResponseEntity<WeatherResponse> getCurrentWeather(@RequestParam String location) {
        
        LOGGER.info("Received request for current weather: {}", location);
        
        CurrentWeatherRequest request = new CurrentWeatherRequest(location);
        validateCurrentWeatherRequest(request);
        
        WeatherResponse response = weatherService.getCurrentWeather(location);
        LOGGER.info("Successfully retrieved current weather for: {}", location);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WeatherResponse> getForecast(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        LOGGER.info("Received request for forecast: {} from {} to {}", location, startDate, endDate);
        
        WeatherForecastRequest request = new WeatherForecastRequest(location, startDate, endDate);
        validateForecastRequest(request);

        WeatherResponse response = weatherService.getForecast(location, startDate, endDate);
        LOGGER.info("Successfully retrieved forecast for: {} from {} to {}", location, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<WeatherResponse> getHistoricalWeather(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        LOGGER.info("Received request for historical weather: {} from {} to {}", location, startDate, endDate);
        
        HistoricalWeatherRequest request = new HistoricalWeatherRequest(location, startDate, endDate);
        validateHistoricalWeatherRequest(request);

        WeatherResponse response = weatherService.getHistorical(location, startDate, endDate);
        LOGGER.info("Successfully retrieved historical weather for: {} from {} to {}",
                   location, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    private void validateCurrentWeatherRequest(CurrentWeatherRequest request) {
        validateLocation(request.getLocation());
    }

    private void validateForecastRequest(WeatherForecastRequest request) {
        validateLocation(request.getLocation());
        validateDateRange(request.getStartDate(), request.getEndDate());
        if (request.getStartDate().isAfter(LocalDate.now().plusDays(15))) {
            throw new WeatherApiException(WeatherErrorCode.START_DATE_TOO_FAR_FUTURE);
        }
    }

    private void validateHistoricalWeatherRequest(HistoricalWeatherRequest request) {
        validateLocation(request.getLocation());
        validateDateRange(request.getStartDate(), request.getEndDate());
        if (request.getStartDate().isAfter(LocalDate.now())) {
            throw new WeatherApiException(WeatherErrorCode.HISTORICAL_DATE_FUTURE);
        }
        if (request.getEndDate().isAfter(LocalDate.now())) {
            throw new WeatherApiException(WeatherErrorCode.HISTORICAL_DATE_FUTURE);
        }
    }

    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new WeatherApiException(WeatherErrorCode.LOCATION_EMPTY);
        }
        if (location.length() < 2) {
            throw new WeatherApiException(WeatherErrorCode.LOCATION_TOO_SHORT);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new WeatherApiException(WeatherErrorCode.MISSING_REQUIRED_PARAMETER, 
                "Both start and end dates are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new WeatherApiException(WeatherErrorCode.START_DATE_AFTER_END_DATE);
        }
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (numberOfDays > 15) {
            throw new WeatherApiException(WeatherErrorCode.MAX_DAYS_EXCEEDED);
        }
    }
}