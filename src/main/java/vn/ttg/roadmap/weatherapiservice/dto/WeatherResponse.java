package vn.ttg.roadmap.weatherapiservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for weather in date range, details for each day and alerts (if present)
 *
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String resolvedAddress;
    private String timezone;
    private String description;
    private List<DayWeather> days;
    private List<WeatherAlert> alerts;
}