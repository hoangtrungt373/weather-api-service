package vn.ttg.roadmap.weatherapiservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

/**
 * A Cache implementation that logs cache operations for monitoring and debugging purposes.
 * This class wraps an existing Cache instance and logs details of each operation including
 * the operation type, key, hit/miss status, and time taken.
 *
 * @author ttg
 */
public class LoggingCache implements Cache {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCache.class);
    private final Cache delegate;

    public LoggingCache(Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        long start = System.nanoTime();
        ValueWrapper value = delegate.get(key);
        long ms = (System.nanoTime() - start) / 1_000_000;
        LOGGER.info("cache.get name={}, key={}, hit={}, timeMs={}", getName(), key, value != null, ms);
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        long start = System.nanoTime();
        T value = delegate.get(key, type);
        long ms = (System.nanoTime() - start) / 1_000_000;
        LOGGER.info("cache.get(name={}, key={}, type={}, hit={}, timeMs={})", getName(), key, type != null ? type.getSimpleName() : null, value != null, ms);
        return value;
    }

    @Override
    public <T> T get(Object key, java.util.concurrent.Callable<T> valueLoader) {
        long start = System.nanoTime();
        try {
            T value = delegate.get(key, valueLoader);
            long ms = (System.nanoTime() - start) / 1_000_000;
            LOGGER.info("cache.get(name={}, key={}, loaded={}, timeMs={})", getName(), key, value != null, ms);
            return value;
        } catch (RuntimeException e) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            LOGGER.warn("cache.get(name={}, key={}, error={}, timeMs={})", getName(), key, e.getMessage(), ms);
            throw e;
        }
    }

    @Override
    public void put(Object key, Object value) {
        long start = System.nanoTime();
        delegate.put(key, value);
        long ms = (System.nanoTime() - start) / 1_000_000;
        LOGGER.info("cache.put name={}, key={}, valueType={}, timeMs={}", getName(), key, value != null ? value.getClass().getSimpleName() : null, ms);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        long start = System.nanoTime();
        ValueWrapper vw = delegate.putIfAbsent(key, value);
        long ms = (System.nanoTime() - start) / 1_000_000;
        LOGGER.info("cache.putIfAbsent name={}, key={}, inserted={}, timeMs={}", getName(), key, vw == null, ms);
        return vw;
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
        LOGGER.info("cache.evict name={}, key={}", getName(), key);
    }

    @Override
    public void clear() {
        delegate.clear();
        LOGGER.info("cache.clear name={}", getName());
    }
}


