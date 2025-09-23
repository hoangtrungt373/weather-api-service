package vn.ttg.roadmap.weatherapiservice.controller.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherApiException;
import vn.ttg.roadmap.weatherapiservice.service.WeatherService;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationContext;

/**
 * Controller implementation the WeatherServiceApi.
 *
 * @author ttg
 */
@RestController
public class WeatherServiceApiController implements WeatherServiceApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServiceApiController.class);

    private static final List<WeatherRequestValidationContext.Type> CURRENT_WEATHER_REQUEST_VALIDATION_STRATEGY = Arrays.asList(
            WeatherRequestValidationContext.Type.LOCATION);
    private static final List<WeatherRequestValidationContext.Type> FORECAST_WEATHER_REQUEST_VALIDATION_STRATEGY = Arrays.asList(
            WeatherRequestValidationContext.Type.LOCATION, WeatherRequestValidationContext.Type.DATE_RANGE, WeatherRequestValidationContext.Type.FORECAST);
    private static final List<WeatherRequestValidationContext.Type> HISTORICAL_WEATHER_REQUEST_VALIDATION_STRATEGY = Arrays.asList(
            WeatherRequestValidationContext.Type.LOCATION, WeatherRequestValidationContext.Type.DATE_RANGE, WeatherRequestValidationContext.Type.HISTORICAL);

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private WeatherRequestValidationContext validationContext;

    @Override
    public ResponseEntity<WeatherResponse> getCurrentWeather(@RequestParam String location) {
        
        LOGGER.info("Received request for current weather: {}", location);

        CurrentWeatherRequest request = new CurrentWeatherRequest(location);
        validateWeatherRequest(CURRENT_WEATHER_REQUEST_VALIDATION_STRATEGY, request);

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
        validateWeatherRequest(FORECAST_WEATHER_REQUEST_VALIDATION_STRATEGY, request);

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
        validateWeatherRequest(HISTORICAL_WEATHER_REQUEST_VALIDATION_STRATEGY, request);

        WeatherResponse response = weatherService.getHistorical(location, startDate, endDate);
        LOGGER.info("Successfully retrieved historical weather for: {} from {} to {}",
                   location, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    private void validateWeatherRequest(List<WeatherRequestValidationContext.Type> stategies, WeatherRequest request) {
        stategies.forEach(strategy -> {
            WeatherRequestValidationResult result = validationContext.validate(strategy, request);

            if (!result.isValid()) {
                throw new WeatherApiException(result.getErrorCode(), result.getMessage());
            }
        });
    }
}