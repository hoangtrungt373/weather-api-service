package vn.ttg.roadmap.weatherapiservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Custom exception class for Weather API errors.
 *
 * @author ttg
 */
@Getter
public class WeatherApiException extends RuntimeException {

    private final WeatherErrorCode errorCode;

    public WeatherApiException(WeatherErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public WeatherApiException(WeatherErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public WeatherApiException(WeatherErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCodeString() {
        return errorCode.getCode();
    }

    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}

