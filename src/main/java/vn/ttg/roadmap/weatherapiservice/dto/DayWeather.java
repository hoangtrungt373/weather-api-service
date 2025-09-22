package vn.ttg.roadmap.weatherapiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for weather in specific day
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
