package org.feuyeux.hello.cache.coon;

import java.util.concurrent.CompletionStage;
import org.feuyeux.hello.cache.lettuce.LettuceCoon;

public class LettuceCache implements AsyncCache<String, String> {

  private final LettuceCoon coon = new LettuceCoon();

  public LettuceCache() {
    coon.init();
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

  public void close() {
    coon.close();
  }

  @Override
  public CompletionStage<String> getAsync(String key) {
    return coon.getAsync(key);
  }

  @Override
  public CompletionStage putAsync(String key, String value) throws Exception {
    return coon.putAsync(key, value);
  }
}
