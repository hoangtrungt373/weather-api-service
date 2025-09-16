package vn.ttg.roadmap.weatherapiservice.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;

/**
 *
 * @author ttg
 */
@Component
public class ResilienceEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ResilienceEventListener.class);
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public ResilienceEventListener(CircuitBreakerRegistry circuitBreakerRegistry,
                                   RetryRegistry retryRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void registerEventListeners() {
        // Circuit Breaker events
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            circuitBreaker.getEventPublisher()
                    .onStateTransition(event ->
                            logger.info("CircuitBreaker {} state transition from {} to {}",
                                    event.getCircuitBreakerName(),
                                    event.getStateTransition().getFromState(),
                                    event.getStateTransition().getToState()));

            circuitBreaker.getEventPublisher()
                    .onFailureRateExceeded(event ->
                            logger.warn("CircuitBreaker {} failure rate {} exceeded threshold",
                                    event.getCircuitBreakerName(),
                                    event.getFailureRate()));
        });

        // Retry events
        retryRegistry.getAllRetries().forEach(retry -> {
            retry.getEventPublisher()
                    .onRetry(event ->
                            logger.info("Retry {} attempt {} for {}",
                                    event.getName(),
                                    event.getNumberOfRetryAttempts(),
                                    event.getLastThrowable().getMessage()));
        });
    }
}
