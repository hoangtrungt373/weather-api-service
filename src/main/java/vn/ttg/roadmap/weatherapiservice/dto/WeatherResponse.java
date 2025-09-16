package vn.ttg.roadmap.weatherapiservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String resolvedAddress;
    private List<DayWeather> days;
}