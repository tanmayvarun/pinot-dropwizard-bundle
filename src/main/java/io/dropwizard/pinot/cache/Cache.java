package io.dropwizard.pinot.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.dropwizard.pinot.healthcheck.configs.exception.ErrorCode;
import io.dropwizard.pinot.healthcheck.configs.exception.PinotDaoException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class Cache<K, V> {

    private LoadingCache<K, V> cache;

    protected Cache(long expiryAfterWriteSeconds, int initialCapacity, CacheLoader<K, V> cacheLoader) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryAfterWriteSeconds, TimeUnit.SECONDS)
                .initialCapacity(initialCapacity)
                .build(cacheLoader);
    }

    public V get(K key) {
        try {
            return cache.get(key);
        } catch (Exception ex) {
            throw PinotDaoException.propagate(ErrorCode.UNKNOWN_INGESTION_ERROR, ex);
        }
    }

    public void put(K key, V value) {
        try {
            cache.put(key, value);
        } catch (Exception ex) {
            throw PinotDaoException.propagate(ErrorCode.UNKNOWN_INGESTION_ERROR, ex);
        }
    }
}
