package org.feuyeux.hello.cache.coon;

import org.feuyeux.hello.cache.RocksdbCoon;

public class RocksdbCache implements BasicCache<String, String> {

  private final RocksdbCoon coon = new RocksdbCoon();

  public RocksdbCache() {
    coon.init();
  }

  public void close() {
    coon.destroy();
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
}
