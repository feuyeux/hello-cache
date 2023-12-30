package org.feuyeux.cache.benchmark.coon;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.caffinitas.ohc.CacheLoader;
import org.feuyeux.memory.OhcCoon;

public class OhcCache<V> implements BasicCache<String, V> {

  private final OhcCoon<V> cache = new OhcCoon<>();

  @Override
  public V get(String key) {
    return cache.get(key);
  }

  public V getWithLoader(String key, CacheLoader<String, V> loader)
      throws ExecutionException, InterruptedException {
    return cache.getWithLoader(key, loader);
  }

  public V getWithLoader(String key, CacheLoader<String, V> loader, long timeout) throws Exception {
    return cache.getWithLoader(key, loader, timeout, TimeUnit.MICROSECONDS);
  }

  @Override
  public void put(String key, V value) {
    cache.put(key, value, 1000);
  }

  @Override
  public void put(String key, V value, long timeout) {
    cache.put(key, value, timeout);
  }

  @Override
  public void remove(String key) {
    cache.remove(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public void stats() {
    cache.stats();
  }

  public int[] hitMissCount(List<String> keys) {
    return cache.hitMissCount(keys);
  }
}
