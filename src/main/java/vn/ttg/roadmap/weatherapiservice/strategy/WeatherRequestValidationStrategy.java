package vn.ttg.roadmap.weatherapiservice.strategy;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;

public interface WeatherRequestValidationStrategy {
    WeatherRequestValidationResult validate(WeatherRequest request);

    WeatherRequestValidationContext.Type getType();
}
