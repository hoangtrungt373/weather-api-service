package vn.ttg.roadmap.weatherapiservice.strategy;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;

/**
 * Strategy interface for validating weather requests.
 *
 * @author ttg
 */
public interface WeatherRequestValidationStrategy {

    /**
     * Validates the given weather request according to the strategy's rules.
     * @return a validation result indicating success or failure
     */
    WeatherRequestValidationResult validate(WeatherRequest request);

    /**
     * Returns the type of validation this strategy performs.
     * @return the validation type for this strategy
     */
    WeatherRequestValidationContext.Type getType();
}
