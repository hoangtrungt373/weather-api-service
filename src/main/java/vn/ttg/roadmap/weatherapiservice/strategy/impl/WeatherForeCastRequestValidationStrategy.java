package vn.ttg.roadmap.weatherapiservice.strategy.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationContext;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationStrategy;
import vn.ttg.roadmap.weatherapiservice.visitor.WeatherRequestDataExtractor;

/**
 *
 * @author ttg
 */
@Component
public class WeatherForeCastRequestValidationStrategy implements WeatherRequestValidationStrategy {

    @Override
    public WeatherRequestValidationResult validate(WeatherRequest request) {
        LocalDate startDate = WeatherRequestDataExtractor.getStartDate(request);
        if (startDate.isAfter(LocalDate.now().plusDays(15))) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.START_DATE_TOO_FAR_FUTURE);
        }
        return WeatherRequestValidationResult.success();
    }

    @Override
    public WeatherRequestValidationContext.Type getType() {
        return WeatherRequestValidationContext.Type.FORECAST;
    }
}
