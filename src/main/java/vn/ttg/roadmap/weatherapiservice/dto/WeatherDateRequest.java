package vn.ttg.roadmap.weatherapiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request DTO for weather on specific date endpoint
 * 
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDateRequest {
    
    @NotBlank(message = "Location cannot be null or empty")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
}
