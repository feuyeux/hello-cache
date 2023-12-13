package org.feuyeux.hello.cache.coon;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroundCache<K extends java.lang.String, V> implements BasicCache<K, V> {

  private final OhcCache<V> ohcCache;
  private final LmdbCache<V> lmdbCache;
  private final ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10);

  public GroundCache() {
    ohcCache = new OhcCache<>();
    lmdbCache = new LmdbCache<>();
  }

  @Override
  public V get(K key) {
    try {
      return ohcCache.getWithLoader(key, k -> {
        V value = lmdbCache.get(k);
        if (value != null) {
          cachedThreadPool.submit(() -> lmdbCache.remove(key));
        }
        return value;
      }, 1000);
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  @Override
  public void put(K key, V value, boolean isLoad) {
    if (isLoad) {
      lmdbCache.put(key, value);
    } else {
      put(key, value);
    }
  }

  @Override
  public void put(K key, V value) {
    ohcCache.put(key, value);
    cachedThreadPool.submit(() -> lmdbCache.remove(key));
  }

  @Override
  public void put(K key, V value, long timeout) {
    ohcCache.put(key, value, timeout);
    cachedThreadPool.submit(() -> lmdbCache.remove(key));
  }

  @Override
  public void remove(K key) {
    ohcCache.remove(key);
    lmdbCache.remove(key);
  }

  @Override
  public void clear() {
    ohcCache.clear();
    lmdbCache.clear();
  }

  @Override
  public void stats() {
    ohcCache.stats();
    lmdbCache.stats();
  }

  public int[] hitMissCount(List<String> keys) {
    return ohcCache.hitMissCount(keys);
  }
}
