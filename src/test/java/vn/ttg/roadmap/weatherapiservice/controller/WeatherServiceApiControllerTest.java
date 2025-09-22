package vn.ttg.roadmap.weatherapiservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import vn.ttg.roadmap.weatherapiservice.controller.impl.WeatherServiceApiController;
import vn.ttg.roadmap.weatherapiservice.dto.DayWeather;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherAlert;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;
import vn.ttg.roadmap.weatherapiservice.service.WeatherApiException;
import vn.ttg.roadmap.weatherapiservice.service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherServiceApiControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherServiceApiController weatherServiceApiController;

    private MockMvc mockMvc;

    private WeatherResponse mockWeatherResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(weatherServiceApiController)
                .setControllerAdvice(new WeatherExceptionHandler())
                .build();

        // Create mock weather response
        DayWeather dayWeather = new DayWeather();
        dayWeather.setDatetime("2024-01-15");
        dayWeather.setTempmax(25.5);
        dayWeather.setTempmin(20.0);
        dayWeather.setConditions("Partly cloudy");
        dayWeather.setDescription("Partly cloudy");

        WeatherAlert alert = new WeatherAlert();
        alert.setEvent("Heat Advisory");
        alert.setDescription("High temperatures expected");

        mockWeatherResponse = new WeatherResponse();
        mockWeatherResponse.setResolvedAddress("Ho Chi Minh City, Vietnam");
        mockWeatherResponse.setTimezone("Asia/Ho_Chi_Minh");
        mockWeatherResponse.setDescription("Partly cloudy");
        mockWeatherResponse.setDays(Arrays.asList(dayWeather));
        mockWeatherResponse.setAlerts(Arrays.asList(alert));
    }

    @Test
    void getCurrentWeather_ValidLocation_ReturnsWeatherResponse() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        when(weatherService.getCurrentWeather(anyString())).thenReturn(mockWeatherResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("location", location)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resolvedAddress").value("Ho Chi Minh City, Vietnam"))
                .andExpect(jsonPath("$.timezone").value("Asia/Ho_Chi_Minh"))
                .andExpect(jsonPath("$.description").value("Partly cloudy"))
                .andExpect(jsonPath("$.days").isArray())
                .andExpect(jsonPath("$.days[0].datetime").value("2024-01-15"))
                .andExpect(jsonPath("$.days[0].tempmax").value(25.5))
                .andExpect(jsonPath("$.alerts").isArray())
                .andExpect(jsonPath("$.alerts[0].event").value("Heat Advisory"));
    }

    @Test
    void getCurrentWeather_EmptyLocation_ReturnsBadRequest() throws Exception {
        // Given
        String location = "";

        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("location", location)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Location cannot be null or empty"));
    }

    @Test
    void getCurrentWeather_ShortLocation_ReturnsBadRequest() throws Exception {
        // Given
        String location = "A";

        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("location", location)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Location must be at least 2 characters long"));
    }

    @Test
    void getCurrentWeather_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Given
        String location = "Invalid Location";
        when(weatherService.getCurrentWeather(anyString()))
                .thenThrow(new WeatherApiException("Weather service unavailable", new RuntimeException()));

        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .param("location", location)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Weather service unavailable"));
    }

    @Test
    void getForecast_ValidParameters_ReturnsWeatherResponse() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        when(weatherService.getForecast(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockWeatherResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resolvedAddress").value("Ho Chi Minh City, Vietnam"));
    }

    @Test
    void getForecast_EmptyLocation_ReturnsBadRequest() throws Exception {
        // Given
        String location = "";
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Location cannot be null or empty"));
    }

    @Test
    void getForecast_StartDateAfterEndDate_ReturnsBadRequest() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().plusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Start date cannot be after end date"));
    }

    @Test
    void getForecast_StartDateTooFarInFuture_ReturnsBadRequest() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().plusDays(20);
        LocalDate endDate = LocalDate.now().plusDays(25);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Start date cannot be more than 15 days in the future"));
    }

    @Test
    void getHistoricalWeather_ValidParameters_ReturnsWeatherResponse() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);
        when(weatherService.getHistorical(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockWeatherResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/historical")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resolvedAddress").value("Ho Chi Minh City, Vietnam"));
    }

    @Test
    void getHistoricalWeather_EmptyLocation_ReturnsBadRequest() throws Exception {
        // Given
        String location = "";
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/historical")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Location cannot be null or empty"));
    }

    @Test
    void getHistoricalWeather_StartDateAfterEndDate_ReturnsBadRequest() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().minusDays(7);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/historical")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Start date cannot be after end date"));
    }

    @Test
    void getHistoricalWeather_FutureDates_ReturnsBadRequest() throws Exception {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        // When & Then
        mockMvc.perform(get("/api/v1/weather/historical")
                .param("location", location)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Historical data cannot include future dates"));
    }

    @Test
    void getCurrentWeather_MissingLocationParameter_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getForecast_MissingParameters_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/forecast")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHistoricalWeather_MissingParameters_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/weather/historical")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
