package vn.ttg.roadmap.weatherapiservice.exception;

import lombok.Getter;

/**
 * Custom exception class for Weather API errors.
 *
 * @author ttg
 */
@Getter
public class WeatherApiException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

    public WeatherApiException(WeatherErrorCode weatherErrorCode) {
        super(weatherErrorCode.getMessage());
        this.errorCode = weatherErrorCode.getCode();
        this.errorMessage = weatherErrorCode.getMessage();
    }

    public WeatherApiException(WeatherErrorCode weatherErrorCode, String message) {
        super(weatherErrorCode.getMessage());
        this.errorCode = weatherErrorCode.getCode();
        this.errorMessage = message;
    }

    public WeatherApiException(WeatherErrorCode weatherErrorCode, String message, Throwable e) {
        super(weatherErrorCode.getMessage(), e);
        this.errorCode = weatherErrorCode.getCode();
        this.errorMessage = message;
    }
}

