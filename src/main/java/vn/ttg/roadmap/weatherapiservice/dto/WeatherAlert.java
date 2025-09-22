package vn.ttg.roadmap.weatherapiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for weather alert if present
 *
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlert {
    private String event;
    private String description;
}
