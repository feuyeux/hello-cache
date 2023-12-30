package org.feuyeux.cache.benchmark.coon;

import org.feuyeux.disk.RocksdbCoon;

public class RocksdbCache implements BasicCache<String, String> {

  private final RocksdbCoon coon = new RocksdbCoon();

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
  public void clear() {}

  @Override
  public void stats() {
    coon.stats();
  }
}
