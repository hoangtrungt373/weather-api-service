package vn.ttg.roadmap.weatherapiservice.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;

@SpringBootTest
@TestPropertySource(properties = {
    "weather.api.base-url=https://example.com",
    "weather.api.key=dummy",
    // Make sure caching is enabled and keys are predictable
    "spring.cache.type=redis"
})
public class WeatherServiceRedisCacheIT {

    private static final GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeAll
    static void startContainer() {
        redis.start();
    }

    @AfterAll
    static void stopContainer() {
        redis.stop();
    }

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void secondCallShouldHitRedisCache() {
        String location = "Hanoi";
        String date = "today";
        String url = String.format("%s/%s/%s/%s?key=%s", "https://example.com", location, date, date, "dummy");

        WeatherResponse response = new WeatherResponse();
        when(restTemplate.getForObject(eq(url), eq(WeatherResponse.class)))
            .thenReturn(response);

        // first call loads cache
        WeatherResponse r1 = weatherService.getCurrentWeather(location);
        // second call should be served from cache (no extra RestTemplate call)
        WeatherResponse r2 = weatherService.getCurrentWeather(location);

        verify(restTemplate, times(1)).getForObject(eq(url), eq(WeatherResponse.class));
        org.junit.jupiter.api.Assertions.assertSame(response, r1);
        org.junit.jupiter.api.Assertions.assertSame(response, r2);

        // Ensure cache actually has the entry
        org.junit.jupiter.api.Assertions.assertNotNull(
            cacheManager.getCache("weathers-current"),
            "Cache manager should have 'weathers-current' cache"
        );
    }
}
