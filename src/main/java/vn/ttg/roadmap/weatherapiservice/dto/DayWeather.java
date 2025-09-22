package vn.ttg.roadmap.weatherapiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing daily weather information
 *
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayWeather {
    private String datetime;
    private double tempmax;
    private double tempmin;
    private String conditions;
    private String description;
}
