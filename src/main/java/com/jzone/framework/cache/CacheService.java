package com.jzone.framework.cache;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Application cache abstraction. Implementations can be replaced without changing business code.
 */
public interface CacheService {

    void put(String key, Object value);

    void put(String key, Object value, Duration ttl);

    <T> T get(String key, Class<T> type);

    <T> T getOrLoad(String key, Class<T> type, Supplier<T> loader);

    <T> T getOrLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader);

    void evict(String key);

    void evictByPrefix(String prefix);

    void clear();
}
