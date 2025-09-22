package vn.ttg.roadmap.weatherapiservice.listener;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * A CacheManager that wraps another CacheManager and adds logging functionality.
 *
 * @author ttg
 */
public class LoggingCacheManager implements CacheManager {

    private final CacheManager delegate;

    public LoggingCacheManager(CacheManager delegate) {
        this.delegate = delegate;
    }

    /**
     * Retrieves a cache by name and wraps it with logging functionality.
     *
     * @param name the name of the cache
     * @return the wrapped cache, or null if no cache with the given name exists
     */
    @Override
    public Cache getCache(String name) {
        Cache cache = delegate.getCache(name);
        return cache == null ? null : new LoggingCache(cache);
    }

    /**
     * Returns the names of all caches managed by this CacheManager.
     *
     * @return a collection of cache names
     */
    @Override
    public Collection<String> getCacheNames() {
        return new ArrayList<>(delegate.getCacheNames());
    }
}


