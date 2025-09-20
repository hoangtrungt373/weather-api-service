package vn.ttg.roadmap.weatherapiservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import vn.ttg.roadmap.weatherapiservice.dto.DayWeather;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherAlert;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;

@SpringBootTest
@Testcontainers
class WeatherServiceRedisCacheIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
        registry.add("weather.api.base-url", () -> "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline");
        registry.add("weather.api.key", () -> "test-api-key");
        registry.add("cache.ttl.weathers-current", () -> "PT30M");
        registry.add("cache.ttl.weathers-forecast", () -> "PT2H");
        registry.add("cache.ttl.weathers-historical", () -> "P1D");
    }

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private RestTemplate restTemplate;

    private WeatherResponse mockWeatherResponse;

    @BeforeEach
    void setUp() {
        // Clear Redis cache before each test
        redisTemplate.getConnectionFactory().getConnection().flushAll();

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
        mockWeatherResponse.setDays(java.util.Arrays.asList(dayWeather));
        mockWeatherResponse.setAlerts(java.util.Arrays.asList(alert));
    }

    @Test
    void getCurrentWeather_FirstCall_ShouldCallRestTemplate() {
        // Given
        String location = "Ho Chi Minh City";
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When
        WeatherResponse result = weatherService.getCurrentWeather(location);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResolvedAddress()).isEqualTo("Ho Chi Minh City, Vietnam");
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void getCurrentWeather_SecondCall_ShouldUseCache() {
        // Given
        String location = "Ho Chi Minh City";
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When - First call
        WeatherResponse result1 = weatherService.getCurrentWeather(location);
        
        // When - Second call (should use cache)
        WeatherResponse result2 = weatherService.getCurrentWeather(location);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getResolvedAddress()).isEqualTo(result2.getResolvedAddress());
        
        // RestTemplate should only be called once due to caching
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void getForecast_FirstCall_ShouldCallRestTemplate() {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When
        WeatherResponse result = weatherService.getForecast(location, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResolvedAddress()).isEqualTo("Ho Chi Minh City, Vietnam");
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void getForecast_SecondCall_ShouldUseCache() {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When - First call
        WeatherResponse result1 = weatherService.getForecast(location, startDate, endDate);
        
        // When - Second call (should use cache)
        WeatherResponse result2 = weatherService.getForecast(location, startDate, endDate);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getResolvedAddress()).isEqualTo(result2.getResolvedAddress());
        
        // RestTemplate should only be called once due to caching
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void getHistorical_FirstCall_ShouldCallRestTemplate() {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When
        WeatherResponse result = weatherService.getHistorical(location, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResolvedAddress()).isEqualTo("Ho Chi Minh City, Vietnam");
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void getHistorical_SecondCall_ShouldUseCache() {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When - First call
        WeatherResponse result1 = weatherService.getHistorical(location, startDate, endDate);
        
        // When - Second call (should use cache)
        WeatherResponse result2 = weatherService.getHistorical(location, startDate, endDate);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getResolvedAddress()).isEqualTo(result2.getResolvedAddress());
        
        // RestTemplate should only be called once due to caching
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void differentLocations_ShouldHaveDifferentCacheEntries() {
        // Given
        String location1 = "Ho Chi Minh City";
        String location2 = "Hanoi";
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When
        WeatherResponse result1 = weatherService.getCurrentWeather(location1);
        WeatherResponse result2 = weatherService.getCurrentWeather(location2);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        
        // RestTemplate should be called twice (once for each location)
        verify(restTemplate, times(2)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void differentDateRanges_ShouldHaveDifferentCacheEntries() {
        // Given
        String location = "Ho Chi Minh City";
        LocalDate startDate1 = LocalDate.now().plusDays(1);
        LocalDate endDate1 = LocalDate.now().plusDays(3);
        LocalDate startDate2 = LocalDate.now().plusDays(4);
        LocalDate endDate2 = LocalDate.now().plusDays(6);
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(mockWeatherResponse);

        // When
        WeatherResponse result1 = weatherService.getForecast(location, startDate1, endDate1);
        WeatherResponse result2 = weatherService.getForecast(location, startDate2, endDate2);

        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        
        // RestTemplate should be called twice (once for each date range)
        verify(restTemplate, times(2)).getForObject(anyString(), any(Class.class));
    }

    @Test
    void cacheManager_ShouldBeLoggingCacheManager() {
        // Then
        assertThat(cacheManager).isInstanceOf(vn.ttg.roadmap.weatherapiservice.listener.LoggingCacheManager.class);
    }

    @Test
    void cacheNames_ShouldContainExpectedCaches() {
        // When
        var cacheNames = cacheManager.getCacheNames();

        // Then
        assertThat(cacheNames).contains("weathers-current", "weathers-forecast", "weathers-historical");
    }

    @Test
    void cache_ShouldBeAccessible() {
        // When
        var currentCache = cacheManager.getCache("weathers-current");
        var forecastCache = cacheManager.getCache("weathers-forecast");
        var historicalCache = cacheManager.getCache("weathers-historical");

        // Then
        assertThat(currentCache).isNotNull();
        assertThat(forecastCache).isNotNull();
        assertThat(historicalCache).isNotNull();
    }
}
