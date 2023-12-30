package org.feuyeux.cache.benchmark.coon;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class GroundBytesCache implements BasicCache<String, byte[]> {

  private final OhcCache<byte[]> ohcCache;
  private final LmdbCache<byte[]> lmdbCache;
  private final ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10);

  public GroundBytesCache() {
    ohcCache = new OhcCache<>();
    lmdbCache = new LmdbCache<>();
  }

  @Override
  public byte[] get(String key) {
    try {
      return ohcCache.getWithLoader(
          key,
          k -> {
            byte[] value = lmdbCache.get(k);
            if (value != null) {
              cachedThreadPool.submit(() -> lmdbCache.remove(key));
            }
            return value;
          });
    } catch (ExecutionException | InterruptedException e) {
      log.error("", e);
      return null;
    }
  }

  @Override
  public void put(String key, byte[] value) {
    ohcCache.put(key, value);
    cachedThreadPool.submit(() -> lmdbCache.remove(key));
  }

  @Override
  public void remove(String key) {
    ohcCache.remove(key);
    lmdbCache.remove(key);
  }

  @Override
  public void clear() {
    ohcCache.clear();
    lmdbCache.clear();
  }

  @Override
  public void stats() {
    ohcCache.stats();
    lmdbCache.stats();
  }
}
