package org.feuyeux.hello.cache.coon;

import org.feuyeux.hello.cache.GuavaCoon;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class GuavaCache<K, V> implements BasicCache<K, V> {

  private final GuavaCoon<K, V> coon = new GuavaCoon<>();

  public GuavaCache(int maximumSize) {
    coon.setMaximumSize(maximumSize);
    coon.init();
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

  @Override
  public void cleanUp() {
    coon.cleanUp();
  }
}
