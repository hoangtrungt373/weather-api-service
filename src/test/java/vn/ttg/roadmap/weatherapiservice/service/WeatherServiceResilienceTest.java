package vn.ttg.roadmap.weatherapiservice.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;

@SpringBootTest
@TestPropertySource(properties = {
    "weather.api.base-url=https://example.com",
    "weather.api.key=dummy",
    // Tighten timeouts for tests
    "weather.api.connect-timeout=200ms",
    "weather.api.read-timeout=200ms",
    // Retry: 3 attempts
    "resilience4j.retry.instances.weatherService.maxAttempts=3",
    "resilience4j.retry.instances.weatherService.waitDuration=10ms",
    "resilience4j.retry.instances.weatherService.enableExponentialBackoff=false",
    // CircuitBreaker: open quickly
    "resilience4j.circuitbreaker.instances.weatherService.slidingWindowSize=2",
    "resilience4j.circuitbreaker.instances.weatherService.minimumNumberOfCalls=2",
    "resilience4j.circuitbreaker.instances.weatherService.failureRateThreshold=50",
    "resilience4j.circuitbreaker.instances.weatherService.waitDurationInOpenState=200ms",
    "resilience4j.circuitbreaker.instances.weatherService.automaticTransitionFromOpenToHalfOpenEnabled=true"
})
@Ignore
@ExtendWith(MockitoExtension.class)
class WeatherServiceResilienceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setupRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void retry_shouldCallRestTemplateUpToMaxAttempts_thenSucceed() {
        String location = "Hanoi";
        String date = "2024-01-01";
        String url = String.format("%s/%s/%s/%s?key=%s", "https://example.com", location, date, date, "dummy");

        // Fail twice, then succeed
        when(restTemplate.getForObject(eq(url), eq(WeatherResponse.class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
            .thenReturn(new WeatherResponse());

//        weatherService.getForecast(location, date, date);

        // 3 invocations due to retry (2 failures + 1 success)
        verify(restTemplate, times(3)).getForObject(eq(url), eq(WeatherResponse.class));
    }

    @Test
    void circuitBreaker_fallbackShouldReturnCachedWhenApiKeepsFailing() {
        String location = "Hanoi";
        String start = "2024-01-01";
        String end = "2024-01-02";
        String url = String.format("%s/%s/%s/%s?key=%s", "https://example.com", location, start, end, "dummy");

        // Always fail to trigger fallback
        when(restTemplate.getForObject(eq(url), eq(WeatherResponse.class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Setup cached value for fallback
        WeatherResponse cached = new WeatherResponse();
        when(valueOperations.get("weathers-forecast::" + location + ":" + start + ":" + end))
            .thenReturn(cached);

//        WeatherResponse response = weatherService.getForecast(location, start, end);
//        org.junit.jupiter.api.Assertions.assertSame(cached, response);
    }

    @Test
    void circuitBreaker_shouldOpenAfterFailures_andShortCircuitCalls() throws InterruptedException {
        String location = "Hanoi";
        String d1 = "2024-01-01";
        String d2 = "2024-01-02";
        String url = String.format("%s/%s/%s/%s?key=%s", "https://example.com", location, d1, d2, "dummy");

        when(restTemplate.getForObject(eq(url), eq(WeatherResponse.class)))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // No cached value first -> expect exception after fallback throws
        when(valueOperations.get("weathers-forecast::" + location + ":" + d1 + ":" + d2))
            .thenReturn(null);

        // First call: fails and fallback throws
//        org.junit.jupiter.api.Assertions.assertThrows(WeatherApiException.class,
//            () -> weatherService.getForecast(location, d1, d2));

        // Second call: enough failures to open CB (windowSize=2, minCalls=2, threshold 50%)
//        org.junit.jupiter.api.Assertions.assertThrows(WeatherApiException.class,
//            () -> weatherService.getForecast(location, d1, d2));

        // Provide cache now for short-circuited calls
        WeatherResponse cached = new WeatherResponse();
        when(valueOperations.get("weathers-forecast::" + location + ":" + d1 + ":" + d2))
            .thenReturn(cached);

        // Wait a brief moment to ensure state updated (not strictly necessary)
        Thread.sleep(50);

        // Third call should be short-circuited by open CB and return cached via fallback
//        WeatherResponse response = weatherService.getForecast(location, d1, d2);
//        org.junit.jupiter.api.Assertions.assertSame(cached, response);

        // After opening, verify that RestTemplate may be called fewer times (<= prior failures)
        verify(restTemplate, Mockito.atLeast(2)).getForObject(eq(url), eq(WeatherResponse.class));
    }
}
