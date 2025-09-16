package vn.ttg.roadmap.weatherapiservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.ttg.roadmap.weatherapiservice.service.WeatherService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for WeatherController validation
 * 
 * @author ttg
 */
@WebMvcTest(WeatherController.class)
public class WeatherControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    public void testCurrentWeatherWithEmptyLocation() throws Exception {
        mockMvc.perform(get("/api/weather/current/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCurrentWeatherWithShortLocation() throws Exception {
        mockMvc.perform(get("/api/weather/current/a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Location must be at least 2 characters long"));
    }

    @Test
    public void testForecastWithInvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/weather/forecast/London")
                .param("startDate", "2024-01-20")
                .param("endDate", "2024-01-15"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Start date cannot be after end date"));
    }

    @Test
    public void testHistoricalWeatherWithFutureDate() throws Exception {
        mockMvc.perform(get("/api/weather/historical/London")
                .param("startDate", "2025-01-15")
                .param("endDate", "2025-01-20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Historical data cannot include future dates"));
    }

    @Test
    public void testWeatherDateWithFutureDate() throws Exception {
        mockMvc.perform(get("/api/weather/date/London")
                .param("date", "2025-01-15"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Date cannot be more than 15 days in the future"));
    }
}
