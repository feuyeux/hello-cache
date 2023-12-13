package org.feuyeux.hello.cache.coon;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ConcurrentMap;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
public record ConcurrentMapCache<K, V>(ConcurrentMap<K, V> map) implements BasicCache<K, V> {

  public ConcurrentMapCache(ConcurrentMap<K, V> map) {
    this.map = requireNonNull(map);
  }

  @Override
  public V get(K key) {
    return map.get(key);
  }

  @Override
  public void put(K key, V value) {
    map.put(key, value);
  }

  @Override
  public void remove(K key) {
    map.remove(key);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public void stats() {

  }
}
