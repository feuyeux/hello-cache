package org.feuyeux.hello.cache.coon;


import java.util.concurrent.CompletionStage;
import org.feuyeux.hello.cache.JedisCoon;

public class JedisCache implements AsyncCache<String, String> {

  private final JedisCoon coon = new JedisCoon();

  public JedisCache() {
    coon.init();
  }

  public void close() {
    coon.close();
  }

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
  public void clear() {
  }

  @Override
  public void stats() {
    coon.stats();
  }

  @Override
  public CompletionStage<String> getAsync(String key) {
    return null;
  }

  @Override
  public CompletionStage putAsync(String key, String value) {
    return null;
  }
}
