package vn.ttg.roadmap.weatherapiservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse;

@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private RestTemplate restTemplate;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackGetCurrentWeather")
//    @Retry(name = "weatherService")
//    @RateLimiter(name = "weatherService")
//    @TimeLimiter(name = "weatherService")
//    @Bulkhead(name = "weatherService")
    @Cacheable(value = "weathers-current", keyGenerator = "weatherKeyGenerator")
    public WeatherResponse getCurrentWeather(String location) {
        LOGGER.info("Fetching current weather for location: {}", location);
        return fetchWeather(location, LocalDate.now(), LocalDate.now());
    }

//    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackGetForecast")
//    @Retry(name = "weatherService")
//    @RateLimiter(name = "weatherService")
//    @TimeLimiter(name = "weatherService")
//    @Bulkhead(name = "weatherService")
    @Cacheable(value = "weathers-forecast", keyGenerator = "weatherKeyGenerator")
    public WeatherResponse getForecast(String location, LocalDate date1, LocalDate date2) {
        LOGGER.info("Fetching forecast for location: {}, dates: {} to {}", location, date1, date2);
        return fetchWeather(location, date1, date2);
    }

//    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackGetHistorical")
//    @Retry(name = "weatherService")
//    @RateLimiter(name = "weatherService")
//    @TimeLimiter(name = "weatherService")
//    @Bulkhead(name = "weatherService")
    @Cacheable(value = "weathers-historical", keyGenerator = "weatherKeyGenerator")
    public WeatherResponse getHistorical(String location, LocalDate date1, LocalDate date2) {
        LOGGER.info("Fetching historical weather for location: {}, dates: {} to {}", location, date1, date2);
        return fetchWeather(location, date1, date2);
    }

    private WeatherResponse fetchWeather(String location, LocalDate date1, LocalDate date2) {

        String url = String.format("%s/%s/%s/%s?key=%s", baseUrl, location, date1.format(DATE_FORMATTER), date2.format(DATE_FORMATTER), apiKey);
        LOGGER.debug("Making API call to: {}", url);

        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            LOGGER.debug("Successfully fetched weather data for location: {}", location);
            return response;
        } catch (HttpClientErrorException e) {
            LOGGER.error("Client error when fetching weather for location: {}, status: {}, message: {}",
                    location, e.getStatusCode(), e.getMessage());
            throw new WeatherApiException("Invalid city or parameters: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            LOGGER.error("Server error when fetching weather for location: {}, status: {}, message: {}",
                    location, e.getStatusCode(), e.getMessage());
            throw new WeatherApiException("Weather API server error. Try later.", e);
        } catch (ResourceAccessException e) {
            LOGGER.error("Connection error when fetching weather for location: {}, message: {}",
                    location, e.getMessage());
            throw new WeatherApiException("Weather API unavailable. Try later.", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error when fetching weather for location: {}, message: {}",
                    location, e.getMessage(), e);
            throw new WeatherApiException("Unexpected error occurred while fetching weather data.", e);
        }
    }

    /**
     * Fallback method for getCurrentWeather
     */
    public WeatherResponse fallbackGetCurrentWeather(String location, Throwable t) {
        LOGGER.warn("Circuit breaker opened for getCurrentWeather, location: {}, error: {}", location, t.getMessage());
        String key = "weathers-current::" + location + ":today";
        WeatherResponse cached = (WeatherResponse) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            LOGGER.info("Returning cached current weather data for location: {}", location);
            return cached;
        }
        LOGGER.error("No cached fallback available for current weather, location: {}", location);
        throw new WeatherApiException("Weather service unavailable and no cached data found", t);
    }

    /**
     * Fallback method for getForecast
     */
    public WeatherResponse fallbackGetForecast(String location, String date1, String date2, Throwable t) {
        LOGGER.warn("Circuit breaker opened for getForecast, location: {}, dates: {} to {}, error: {}",
                location, date1, date2, t.getMessage());
        String key = "weathers-forecast::" + location + ":" + date1 + ":" + date2;
        WeatherResponse cached = (WeatherResponse) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            LOGGER.info("Returning cached forecast data for location: {}, dates: {} to {}", location, date1, date2);
            return cached;
        }
        LOGGER.error("No cached fallback available for forecast, location: {}, dates: {} to {}", location, date1, date2);
        throw new WeatherApiException("Weather service unavailable and no cached data found", t);
    }

    /**
     * Fallback method for getHistorical
     */
    public WeatherResponse fallbackGetHistorical(String location, String date1, String date2, Throwable t) {
        LOGGER.warn("Circuit breaker opened for getHistorical, location: {}, dates: {} to {}, error: {}",
                location, date1, date2, t.getMessage());
        String key = "weathers-historical::" + location + ":" + date1 + ":" + date2;
        WeatherResponse cached = (WeatherResponse) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            LOGGER.info("Returning cached historical data for location: {}, dates: {} to {}", location, date1, date2);
            return cached;
        }
        LOGGER.error("No cached fallback available for historical data, location: {}, dates: {} to {}", location, date1, date2);
        throw new WeatherApiException("Weather service unavailable and no cached data found", t);
    }
}
