package org.feuyeux.cache.benchmark.coon;

import org.feuyeux.memory.CaffeineCoon;

/**
 * @author feuyeux
 */
public final class CaffeineCache<K, V> implements BasicCache<K, V> {

  private final CaffeineCoon<K, V> coon = new CaffeineCoon<>();

  public CaffeineCache(int maximumSize) {
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
  public void cleanUp() {
    coon.cleanUp();
  }

  @Override
  public void stats() {
    coon.stats();
  }
}
