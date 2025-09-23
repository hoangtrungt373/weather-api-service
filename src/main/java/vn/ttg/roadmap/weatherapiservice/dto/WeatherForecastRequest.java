package vn.ttg.roadmap.weatherapiservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ttg.roadmap.weatherapiservice.visitor.WeatherRequestVisitor;

/**
 * Request DTO for weather forecast endpoint
 * 
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastRequest implements WeatherRequest{
    
    @NotBlank(message = "Location cannot be null or empty")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Override
    public <T> T accept(WeatherRequestVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
