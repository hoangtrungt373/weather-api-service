package vn.ttg.roadmap.weatherapiservice.strategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;

/**
 * Context class for managing and executing weather request validation strategies.
 *
 * @author ttg
 */
@Component
public class WeatherRequestValidationContext {
    private final Map<Type, WeatherRequestValidationStrategy> strategies;

    public WeatherRequestValidationContext(List<WeatherRequestValidationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getType(),
                        strategy -> strategy
                ));
    }

    public WeatherRequestValidationResult validate(Type type, WeatherRequest value) {
        WeatherRequestValidationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            return WeatherRequestValidationResult.failure(WeatherErrorCode.INVALID_PARAMETERS);
        }
        return strategy.validate(value);
    }

    /**
     * Enumeration of available validation types.
     */
    public enum Type {
        LOCATION,
        DATE_RANGE,
        FORECAST,
        HISTORICAL,
    }
}
