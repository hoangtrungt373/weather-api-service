package vn.ttg.roadmap.weatherapiservice.conf;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ttg.roadmap.weatherapiservice.listener.LoggingCacheManager;

/**
 * Configuration class for Redis caching
 *
 * @author ttg
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Configure the RedisCacheConfiguration with custom key and value serializers.
     * The value serializer uses GenericJackson2JsonRedisSerializer with an custom ObjectMapper
     * that has default typing activated to handle polymorphic types.
     * <p>
     * // Without type info (default)
     * {"resolvedAddress": "London", "description": "Sunny"}
     * <p>
     * // With type info (this configuration), prevent ClassCastException when reading from cache.
     * {
     *   "@class": "vn.ttg.roadmap.weatherapiservice.dto.WeatherResponse",
     *   "resolvedAddress": "London",
     *   "description": "Sunny"
     * }
     *
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {

        ObjectMapper cacheMapper = objectMapper.copy();
        cacheMapper.activateDefaultTyping(
                cacheMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        var valueSerializer = new GenericJackson2JsonRedisSerializer(cacheMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));
    }

    @Bean(name = "delegateRedisCacheManager")
    public RedisCacheManager delegateRedisCacheManager(RedisConnectionFactory connectionFactory, RedisCacheConfiguration baseConfig, RedisCacheManagerBuilderCustomizer customizer) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(baseConfig);
        customizer.customize(builder);
        return builder.build();
    }

    /**
     * Customize the RedisCacheManager to set different TTLs for different cache names.
     * The TTL values are injected from application properties.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            @Value("${cache.ttl.weathers-current}") Duration currentTtl,
            @Value("${cache.ttl.weathers-forecast}") Duration forecastTtl,
            @Value("${cache.ttl.weathers-historical}") Duration historicalTtl,
            RedisCacheConfiguration baseConfig) {

        return builder -> builder
                .withCacheConfiguration("weathers-current", baseConfig.entryTtl(currentTtl))
                .withCacheConfiguration("weathers-forecast", baseConfig.entryTtl(forecastTtl))
                .withCacheConfiguration("weathers-historical", baseConfig.entryTtl(historicalTtl));
    }

    /**
     * Custom key generator for weather caching.
     * Generates keys based on method parameters:
     * - For current weather: "location:today"
     * - For forecast/historical: "location:startDate:endDate"
     */
    @Bean("weatherKeyGenerator")
    public KeyGenerator weatherKeyGenerator() {
        return (target, method, params) -> {
            String location = String.valueOf(params[0]).trim().toLowerCase();
            if (params.length == 3 && params[1] instanceof LocalDate && params[2] instanceof LocalDate) {
                return location + ":" + params[1] + ":" + params[2];
            }
            return location + ":today";
        };
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        var keySerializer = new StringRedisSerializer();
        var valueSerializer = new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Primary CacheManager that wraps the delegate RedisCacheManager to add logging functionality.
     * This allows monitoring cache hits and misses.
     */
    @Bean
    @Primary
    public CacheManager cacheManager(@Qualifier("delegateRedisCacheManager") RedisCacheManager delegate) {
        return new LoggingCacheManager(delegate);
    }
}

