package vn.ttg.roadmap.weatherapiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Standard error response DTO
 * 
 * @author ttg
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
