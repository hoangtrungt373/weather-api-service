package vn.ttg.roadmap.weatherapiservice.indicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;

/**
 *
 * @author ttg
 */
@Component("resilience4jHealth")
public class ResilienceHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public ResilienceHealthIndicator(CircuitBreakerRegistry circuitBreakerRegistry,
                                     RetryRegistry retryRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @Override
    public Health health() {
        Health.Builder health = Health.up();

        // Check circuit breaker states
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            health.withDetail("circuitBreaker." + cb.getName(),
                    cb.getState().toString());
        });

        return health.build();
    }
}
