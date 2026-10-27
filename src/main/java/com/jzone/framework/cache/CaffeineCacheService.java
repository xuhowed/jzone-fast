package com.jzone.framework.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Caffeine-backed cache implementation. Set another jzone.cache.provider to replace it.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "jzone.cache", name = "provider", havingValue = "caffeine", matchIfMissing = true)
public class CaffeineCacheService implements CacheService {

    private final Cache<String, Object> cache;

    private record CacheEntry(Object value, long expiresAt) {

        private boolean isExpired() {
            return expiresAt > 0 && System.currentTimeMillis() > expiresAt;
        }
    }

    @Override
    public void put(String key, Object value) {
        if (value == null) {
            evict(key);
            return;
        }
        cache.put(key, new CacheEntry(value, 0));
    }

    @Override
    public void put(String key, Object value, Duration ttl) {
        if (value == null) {
            evict(key);
            return;
        }
        long expiresAt = ttl == null ? 0 : System.currentTimeMillis() + ttl.toMillis();
        cache.put(key, new CacheEntry(value, expiresAt));
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object rawValue = cache.getIfPresent(key);
        if (!(rawValue instanceof CacheEntry entry)) {
            return null;
        }
        if (entry.isExpired()) {
            evict(key);
            return null;
        }
        return type.isInstance(entry.value()) ? type.cast(entry.value()) : null;
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> type, Supplier<T> loader) {
        T value = get(key, type);
        if (value != null) {
            return value;
        }
        T loaded = loader.get();
        if (loaded != null) {
            put(key, loaded);
        }
        return loaded;
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader) {
        return getOrLoad(key, type, loader);
    }

    @Override
    public void evict(String key) {
        cache.invalidate(key);
    }

    @Override
    public void evictByPrefix(String prefix) {
        cache.asMap().keySet().removeIf(key -> key.startsWith(prefix));
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }
}
