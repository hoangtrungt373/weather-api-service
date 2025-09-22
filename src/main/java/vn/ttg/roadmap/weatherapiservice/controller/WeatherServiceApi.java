package vn.ttg.roadmap.weatherapiservice.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;

/**
 * Rest controller for weather API service
 *
 * <p>This controller provides HTTP endpoints for weather API including:</p>
 * <ul>
 *     <li>GET /api/v1/weather/current - Get current weather</li>
 *     <li>GET /api/v1/weather/forecast - Get weather forecast</li>
 *     <li>GET /api/v1/weather/historical - Get weather historical</li>
 * </ul>
 *
 * @author ttg
 */
@RequestMapping("/api/v1/weather")
public interface WeatherServiceApi {

    /**
     * Get current date weather for a specific location
     * @param location Location name (city, country or coordinates)
     * @return WeatherResponse containing current date weather data
     */
    @GetMapping("/current")
    ResponseEntity<WeatherResponse> getCurrentWeather(@RequestParam String location);

    /**
     * Get weather forecast for a specific location and date range
     * @param location Location name (city, country or coordinates)
     * @param startDate Start date in yyyy-MM-dd format
     * @param endDate End date in yyyy-MM-dd format
     * @return WeatherResponse containing forecast data
     */
    @GetMapping("/forecast")
    ResponseEntity<WeatherResponse> getForecast(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);

    /**
     * Get historical weather data for a specific location and date range
     * @param location Location name (city, country or coordinates)
     * @param startDate Start date in yyyy-MM-dd format
     * @param endDate End date in yyyy-MM-dd format
     * @return WeatherResponse containing historical weather data
     */
    @GetMapping("/historical")
    ResponseEntity<WeatherResponse> getHistoricalWeather(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);
}
