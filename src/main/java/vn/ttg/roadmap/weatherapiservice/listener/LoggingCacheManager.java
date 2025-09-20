package vn.ttg.roadmap.weatherapiservice.listener;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Decorator CacheManager that wraps caches with logging behavior.
 */
public class LoggingCacheManager implements CacheManager {

    private final CacheManager delegate;

    public LoggingCacheManager(CacheManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = delegate.getCache(name);
        return cache == null ? null : new LoggingCache(cache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return new ArrayList<>(delegate.getCacheNames());
    }
}


