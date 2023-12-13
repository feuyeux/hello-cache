package org.feuyeux.hello.cache.coon;

import org.feuyeux.hello.cache.ChronicleMapCoon;

public class ChronicCache implements BasicCache<String, String> {

  private final ChronicleMapCoon<String, String> coon = new ChronicleMapCoon(String.class,
      String.class);

  public ChronicCache() {
    coon.init();
  }

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
  public void clear() {

  }

  @Override
  public void stats() {
    coon.stats();
  }
}
