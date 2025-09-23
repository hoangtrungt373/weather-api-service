package vn.ttg.roadmap.weatherapiservice.strategy.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationContext;
import vn.ttg.roadmap.weatherapiservice.strategy.WeatherRequestValidationStrategy;
import vn.ttg.roadmap.weatherapiservice.visitor.WeatherRequestDataExtractor;

@Component
public class WeatherDateRangeRequestValidationStrategy implements WeatherRequestValidationStrategy {

    @Override
    public WeatherRequestValidationResult validate(WeatherRequest request) {
        LocalDate startDate = WeatherRequestDataExtractor.getStartDate(request);
        LocalDate endDate = WeatherRequestDataExtractor.getEndDate(request);
        if (startDate == null || endDate == null) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.MISSING_REQUIRED_PARAMETER,
                    "Both start and end dates are required");
        }
        if (startDate.isAfter(endDate)) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.START_DATE_AFTER_END_DATE);
        }
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (numberOfDays > 15) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.MAX_DAYS_EXCEEDED);
        }
        return WeatherRequestValidationResult.success();
    }

    @Override
    public WeatherRequestValidationContext.Type getType() {
        return WeatherRequestValidationContext.Type.DATE_RANGE;
    }
}
