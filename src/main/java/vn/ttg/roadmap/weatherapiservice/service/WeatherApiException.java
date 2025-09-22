package vn.ttg.roadmap.weatherapiservice.service;

/**
 * Custom exception for Weather API related errors.
 *
 * @author ttg
 */
public class WeatherApiException extends RuntimeException {
    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

