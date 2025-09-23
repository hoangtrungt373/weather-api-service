package vn.ttg.roadmap.weatherapiservice.strategy.impl;

import org.springframework.stereotype.Component;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationContext;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationStrategy;

/**
 *
 * @author ttg
 */
@Component
public class WeatherLocationRequestValidationStrategy implements WeatherRequestValidationStrategy {

    @Override
    public WeatherRequestValidationResult validate(WeatherRequest request) {
        String location = WeatherRequestValidationContext.getLocation(request);

        if (location == null || location.trim().isEmpty()) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.LOCATION_EMPTY);
        }
        if (location.length() < 2) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.LOCATION_TOO_SHORT);
        }
        return WeatherRequestValidationResult.success();
    }

    @Override
    public WeatherRequestValidationContext.Type getType() {
        return WeatherRequestValidationContext.Type.LOCATION;
    }

}
