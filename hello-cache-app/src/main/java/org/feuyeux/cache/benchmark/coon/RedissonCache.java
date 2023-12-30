package org.feuyeux.cache.benchmark.coon;

import java.util.concurrent.CompletionStage;
import org.feuyeux.redis.RedissonCoon;

public class RedissonCache implements AsyncCache<String, String> {

  private final RedissonCoon coon = new RedissonCoon();

  @Override
  public String get(String key) {
    return coon.get(key);
  }

  @Override
  public void put(String key, String value) {
    coon.put(key, value);
  }

  @Override
  public void remove(String key) {
    coon.remove(key);
  }

  @Override
  public void clear() {}

  public void close() {
    coon.close();
  }

  @Override
  public CompletionStage<String> getAsync(String key) {
    return coon.getAsync(key);
  }

  @Override
  public CompletionStage putAsync(String key, String value) {
    return coon.putAsync(key, value);
  }
}
