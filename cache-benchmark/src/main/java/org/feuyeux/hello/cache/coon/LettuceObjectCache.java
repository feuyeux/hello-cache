package org.feuyeux.hello.cache.coon;

import java.util.concurrent.CompletionStage;
import org.feuyeux.hello.cache.lettuce.LettuceObjectCoon;

public class LettuceObjectCache<V> implements AsyncCache<String, V> {

  private final LettuceObjectCoon<V> coon = new LettuceObjectCoon<>();

  public LettuceObjectCache() {
    coon.init();
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

  public void close() {
    coon.close();
  }

  @Override
  public CompletionStage<V> getAsync(String key) {
    return coon.getAsync(key);
  }

  @Override
  public CompletionStage putAsync(String key, V value) throws Exception {
    return coon.putAsync(key, value);
  }
}