package org.feuyeux.cache.benchmark.coon;

import org.feuyeux.memory.Cache2kCoon;

public final class Cache2kCache<K, V> implements BasicCache<K, V> {

  private final Cache2kCoon<K, V> coon = new Cache2kCoon<>();

  public Cache2kCache(long maximumSize) {
    coon.setMaximumSize(maximumSize);
  }

  @Override
  public V get(K key) {
    return coon.get(key);
  }

  @Override
  public void put(K key, V value) {
    coon.put(key, value);
  }

  @Override
  public void remove(K key) {
    coon.remove(key);
  }

  @Override
  public void clear() {
    coon.clear();
  }

  @Override
  public void stats() {
    coon.stats();
  }
}
