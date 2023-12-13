package org.feuyeux.hello.cache.coon;

import org.feuyeux.hello.cache.LmdbCoon;

public class LmdbCache<V> implements BasicCache<String, V> {

  private final LmdbCoon<V> coon = new LmdbCoon<>();

  public LmdbCache() {
    coon.init();
  }

  public void close() {
    coon.destroy();
  }

  @Override
  public V get(String key) {
    return coon.get(key);
  }

  @Override
  public void put(String key, V value) {
    coon.put(key, value);
  }

  @Override
  public void remove(String key) {
    coon.remove(key);
  }

  @Override
  public void clear() {

  }

  @Override
  public void stats() {
    coon.stats();
  }
}
