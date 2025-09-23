package vn.ttg.roadmap.weatherapiservice.visitor;

import vn.ttg.roadmap.weatherapiservice.dto.CurrentWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.HistoricalWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherForecastRequest;

/**
 * Visitor interface for extracting data from different types of weather requests.
 * @param <T> the type of data to extract from the request
 *
 * @author ttg
 */
public interface WeatherRequestVisitor<T> {
    T visit(CurrentWeatherRequest request);
    T visit(WeatherForecastRequest request);
    T visit(HistoricalWeatherRequest request);
}
