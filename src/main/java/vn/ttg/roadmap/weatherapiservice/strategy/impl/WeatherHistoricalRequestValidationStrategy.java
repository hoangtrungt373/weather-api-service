package vn.ttg.roadmap.weatherapiservice.strategy.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherApiException;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationContext;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationStrategy;

/**
 *
 * @author ttg
 */
@Component
public class WeatherHistoricalRequestValidationStrategy implements WeatherRequestValidationStrategy {

    @Override
    public WeatherRequestValidationResult validate(WeatherRequest request) {
        LocalDate startDate = WeatherRequestValidationContext.getStartDate(request);
        LocalDate endDate = WeatherRequestValidationContext.getEndDate(request);
        if (startDate.isAfter(LocalDate.now())) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.HISTORICAL_DATE_FUTURE);
        }
        if (endDate.isAfter(LocalDate.now())) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.HISTORICAL_DATE_FUTURE);
        }
        return WeatherRequestValidationResult.success();
    }

    @Override
    public WeatherRequestValidationContext.Type getType() {
        return WeatherRequestValidationContext.Type.HISTORICAL;
    }
}
