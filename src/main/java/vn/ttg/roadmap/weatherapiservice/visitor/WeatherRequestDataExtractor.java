package vn.ttg.roadmap.weatherapiservice.visitor;

import java.time.LocalDate;

import vn.ttg.roadmap.weatherapiservice.dto.CurrentWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.HistoricalWeatherRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherForecastRequest;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherRequest;

/**
 * Utility class for extracting data from weather requests using the Visitor Pattern.
 *
 * @author ttg
 */
public class WeatherRequestDataExtractor {

    private WeatherRequestDataExtractor() {}

    public static String getLocation(WeatherRequest request) {
        return request.accept(new WeatherRequestVisitor<String>() {
            @Override
            public String visit(CurrentWeatherRequest request) {
                return request.getLocation();
            }

            @Override
            public String visit(WeatherForecastRequest request) {
                return request.getLocation();
            }

            @Override
            public String visit(HistoricalWeatherRequest request) {
                return request.getLocation();
            }
        });
    }

    public static LocalDate getStartDate(WeatherRequest request) {
        return request.accept(new WeatherRequestVisitor<LocalDate>() {
            @Override
            public LocalDate visit(CurrentWeatherRequest request) {
                return null;
            }

            @Override
            public LocalDate visit(WeatherForecastRequest request) {
                return request.getStartDate();
            }

            @Override
            public LocalDate visit(HistoricalWeatherRequest request) {
                return request.getStartDate();
            }
        });
    }

    public static LocalDate getEndDate(WeatherRequest request) {
        return request.accept(new WeatherRequestVisitor<LocalDate>() {
            @Override
            public LocalDate visit(CurrentWeatherRequest request) {
                return null;
            }

            @Override
            public LocalDate visit(WeatherForecastRequest request) {
                return request.getEndDate();
            }

            @Override
            public LocalDate visit(HistoricalWeatherRequest request) {
                return request.getEndDate();
            }
        });
    }
}