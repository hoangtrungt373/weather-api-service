package vn.ttg.roadmap.weatherapiservice.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;

/**
 *
 * @author ttg
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class WeatherRequestValidationResult {

    private final boolean valid;
    private final WeatherErrorCode errorCode;
    private final String message;


    public static WeatherRequestValidationResult success() {
        return WeatherRequestValidationResult.builder()
                .valid(true)
                .build();
    }

    public static WeatherRequestValidationResult failure(WeatherErrorCode errorCode) {
        return WeatherRequestValidationResult.builder()
                .valid(false)
                .errorCode(errorCode)
                .message(errorCode.getMessage())
                .build();
    }

    public static WeatherRequestValidationResult failure(WeatherErrorCode errorCode, String message) {
        return WeatherRequestValidationResult.builder()
                .valid(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}
