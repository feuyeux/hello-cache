package org.feuyeux.memory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuavaCoon<K, V> {

  Cache<K, V> cache;
  static final int CONCURRENCY_LEVEL = 64;
  @Setter private int maximumSize = 10000;

  public GuavaCoon() {
    cache =
        CacheBuilder.newBuilder()
            .concurrencyLevel(CONCURRENCY_LEVEL)
            .initialCapacity(maximumSize)
            .maximumSize(maximumSize)
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build();
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.getIfPresent(key);
  }

  public void remove(K key) {
    cache.invalidate(key);
  }

  public void clear() {
    cache.invalidateAll();
  }

  public void cleanUp() {
    cache.cleanUp();
  }

  public void stats() {}
}
