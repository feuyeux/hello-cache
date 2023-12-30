package org.feuyeux.cache.benchmark.coon;

import org.feuyeux.disk.LmdbAgronaCoon;

public class LmdbAgronaCache implements BasicCache<String, String> {

  private final LmdbAgronaCoon coon = new LmdbAgronaCoon();

  public void close() {
    coon.destroy();
  }

  @Override
  public String get(String key) {
    return coon.get(key);
  }

  public void put(String key, String value) {
    coon.put(key, value);
  }

  @Override
  public void remove(String key) {
    coon.remove(key);
  }

  @Override
  public void clear() {}

  @Override
  public void stats() {
    coon.stats();
  }
}
