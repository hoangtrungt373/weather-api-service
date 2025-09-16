package vn.ttg.roadmap.weatherapiservice.service;

public class WeatherApiException extends RuntimeException {
    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

