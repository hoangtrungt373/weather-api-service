package vn.ttg.roadmap.weatherapiservice.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.ttg.roadmap.weatherapiservice.dto.CurrentWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.HistoricalWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherForecastRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequestValidationResult;
import vn.ttg.roadmap.weatherapiservice.exception.WeatherErrorCode;

/**
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

    public enum Type {
        LOCATION,
        DATE_RANGE,
        FORECAST,
        HISTORICAL,
    }

    public static LocalDate getStartDate(WeatherRequest request) {
        if (request instanceof WeatherForecastRequest) {
            return ((WeatherForecastRequest) request).getStartDate();
        }
        if (request instanceof HistoricalWeatherRequest) {
            return ((HistoricalWeatherRequest) request).getStartDate();
        }
        return null;
    }

    public static LocalDate getEndDate(WeatherRequest request) {
        if (request instanceof WeatherForecastRequest) {
            return ((WeatherForecastRequest) request).getEndDate();
        }
        if (request instanceof HistoricalWeatherRequest) {
            return ((HistoricalWeatherRequest) request).getEndDate();
        }
        return null;
    }

    public static String getLocation(WeatherRequest request) {
        if (request instanceof CurrentWeatherRequest) {
            return ((CurrentWeatherRequest) request).getLocation();
        }
        if (request instanceof WeatherForecastRequest) {
            return ((WeatherForecastRequest) request).getLocation();
        }
        if (request instanceof HistoricalWeatherRequest) {
            return ((HistoricalWeatherRequest) request).getLocation();
        }
        return null;
    }
}
