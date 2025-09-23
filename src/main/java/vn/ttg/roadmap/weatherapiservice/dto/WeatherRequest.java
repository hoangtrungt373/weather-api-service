package vn.ttg.roadmap.weatherapiservice.dto;

import vn.ttg.roadmap.weatherapiservice.visitor.WeatherRequestVisitor;

/**
 * Base interface for all weather-related requests.
 *
 * <p>This interface defines the contract for accepting visitors, enabling the Visitor Pattern
 * for data extraction and validation.</p>
 *
 * @author ttg
 */
public interface WeatherRequest {
    <T> T accept(WeatherRequestVisitor<T> visitor);
}
