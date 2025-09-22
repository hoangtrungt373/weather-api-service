package vn.ttg.roadmap.weatherapiservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import vn.ttg.roadmap.weatherapiservice.dto.ErrorResponse;
import vn.ttg.roadmap.weatherapiservice.service.WeatherApiException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Global exception handler for weather API
 * 
 * @author ttg
 */
@RestControllerAdvice
public class WeatherExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherExceptionHandler.class);

    /**
     * Handle WeatherApiException - custom business logic exceptions
     */
    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<ErrorResponse> handleWeatherApiException(WeatherApiException ex) {
        LOGGER.error("Weather API exception: {}", ex.getMessage(), ex);
        
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        if (ex.getMessage().contains("Invalid city") || 
            ex.getMessage().contains("Invalid parameters") ||
            ex.getMessage().contains("cannot be null") ||
            ex.getMessage().contains("must be") ||
            ex.getMessage().contains("cannot be after")) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.warn("Validation error: {}", ex.getMessage());
        
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed: " + String.join(", ", errors),
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.warn("Constraint violation: {}", ex.getMessage());
        
        List<String> errors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.add(fieldName + ": " + errorMessage);
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint violation: " + String.join(", ", errors),
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle type mismatch exceptions (e.g., invalid date format)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        LOGGER.warn("Type mismatch error: {}", ex.getMessage());
        
        String errorMessage = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
            ex.getValue(),
            ex.getName(),
            ex.getRequiredType().getSimpleName()
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            errorMessage,
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle JSON parsing errors
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        LOGGER.warn("JSON parsing error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid JSON format: " + ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again later.",
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
