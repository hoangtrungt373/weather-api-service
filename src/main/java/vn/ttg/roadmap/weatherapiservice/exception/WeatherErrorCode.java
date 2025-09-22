package vn.ttg.roadmap.weatherapiservice.exception;

import lombok.Getter;

/**
 * Enum representing error codes and messages for the Weather API Service.
 *
 * @author ttg
 */
@Getter
public enum WeatherErrorCode {
    
    // Client Errors (4xx)
    INVALID_PARAMETERS("WEATHER_002", "Invalid parameters provided"),
    MISSING_REQUIRED_PARAMETER("WEATHER_003", "Required parameter is missing"),
    LOCATION_TOO_SHORT("WEATHER_006", "Location must be at least 2 characters long"),
    LOCATION_EMPTY("WEATHER_007", "Location cannot be null or empty"),
    START_DATE_AFTER_END_DATE("WEATHER_008", "Start date cannot be after end date"),
    START_DATE_TOO_FAR_FUTURE("WEATHER_009", "Start date cannot be more than 15 days in the future"),
    HISTORICAL_DATE_FUTURE("WEATHER_010", "Historical data cannot include future dates"),
    MAX_DAYS_EXCEEDED("WEATHER_011", "Maximum days cannot be more than 15 days"),
    
    // Server Errors (5xx)
    WEATHER_API_UNAVAILABLE("WEATHER_501", "Weather API service is currently unavailable"),
    WEATHER_API_SERVER_ERROR("WEATHER_502", "Weather API server error. Please try again later"),

    // Generic Errors
    UNEXPECTED_ERROR("WEATHER_999", "An unexpected error occurred. Please try again later");
    
    private final String code;
    private final String message;
    
    /**
     * Constructor for error code enum.
     * 
     * @param code the error code
     * @param message the error message
     */
    WeatherErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
