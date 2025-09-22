package vn.ttg.roadmap.weatherapiservice.service;

import java.time.LocalDate;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;

/**
 * Service class for fetching weather data from external API with caching.
 *
 * @author ttg
 */
public interface WeatherService {

    /**
     * Fetches current weather data for the specified location.
     *
     * @param location the location to fetch weather for
     * @return WeatherResponse containing current weather data
     */
    WeatherResponse getCurrentWeather(String location);

    /**
     * Fetches weather forecast data for the specified location and date range.
     *
     * @param location the location to fetch weather for
     * @param date1 the start date of the forecast range
     * @param date2 the end date of the forecast range
     * @return WeatherResponse containing forecast data
     */
    WeatherResponse getForecast(String location, LocalDate date1, LocalDate date2);

    /**
     * Fetches historical weather data for the specified location and date range.
     *
     * @param location the location to fetch weather for
     * @param date1 the start date of the historical range
     * @param date2 the end date of the historical range
     * @return WeatherResponse containing historical weather data
     */
    WeatherResponse getHistorical(String location, LocalDate date1, LocalDate date2);
}
