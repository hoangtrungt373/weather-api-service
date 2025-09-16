package vn.ttg.roadmap.weatherapiservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherForecastRequest;

import java.time.LocalDate;

/**
 * Validator for date range validation
 * 
 * @author ttg
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, WeatherForecastRequest> {
    
    private int maxDaysInFuture;
    
    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.maxDaysInFuture = constraintAnnotation.maxDaysInFuture();
    }
    
    @Override
    public boolean isValid(WeatherForecastRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getStartDate() == null || request.getEndDate() == null) {
            return true; // Let @NotNull handle null validation
        }
        
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDate now = LocalDate.now();
        
        // Check if start date is after end date
        if (startDate.isAfter(endDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start date cannot be after end date")
                   .addPropertyNode("startDate")
                   .addConstraintViolation();
            return false;
        }
        
        // Check if start date is too far in the future
        if (startDate.isAfter(now.plusDays(maxDaysInFuture))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start date cannot be more than " + maxDaysInFuture + " days in the future")
                   .addPropertyNode("startDate")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
