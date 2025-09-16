package vn.ttg.roadmap.weatherapiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for current weather endpoint
 * 
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeatherRequest {
    
    @NotBlank(message = "Location cannot be null or empty")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;
}
