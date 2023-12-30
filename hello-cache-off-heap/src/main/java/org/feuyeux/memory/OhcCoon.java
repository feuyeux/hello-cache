package org.feuyeux.memory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.caffinitas.ohc.CacheLoader;
import org.caffinitas.ohc.CloseableIterator;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

@Slf4j

// ff-heap-cache
public class OhcCoon<T> {

  private static final int TIMEOUT_MILLISECONDS = 1000;
  private final OHCache<String, T> cache;
  private static final long lmdbMaxSizeMb = 1;
  public static final int ONE_MB = 1024 * 1024;

  public OhcCoon() {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
    cache =
        OHCacheBuilder.<String, T>newBuilder()
            .keySerializer(new OhcStringSerializer())
            .valueSerializer(new OhcObjectSerializer<>())
            .maxEntrySize(lmdbMaxSizeMb * ONE_MB)
            .timeoutsPrecision(TIMEOUT_MILLISECONDS)
            .eviction(Eviction.W_TINY_LFU)
            .executorService(executorService)
            .throwOOME(true)
            .build();
  }

  public void put(String key, T value) {
    if (key == null || value == null) {
      return;
    }
    cache.put(key, value);
  }

  public boolean put(String key, T value, long expireAt) {
    return cache.put(key, value, System.currentTimeMillis() + expireAt);
  }

  public T get(String key) {
    if (key != null) {
      return cache.get(key);
    }
    return null;
  }

  public T getWithLoader(String key, CacheLoader<String, T> loader)
      throws ExecutionException, InterruptedException {
    return cache.getWithLoader(key, loader);
  }

  public T getWithLoader(String key, CacheLoader<String, T> loader, long timeout, TimeUnit unit)
      throws ExecutionException, InterruptedException, TimeoutException {
    return cache.getWithLoader(key, loader, timeout, unit);
  }

  public void stats() {
    log.info(
        "Ohc Memory consumed: {} / {}, size {}",
        byteCountToDisplaySize(cache.memUsed()),
        byteCountToDisplaySize(cache.capacity()),
        cache.size());
    log.info("VM total:{}", byteCountToDisplaySize(Runtime.getRuntime().totalMemory()));
    log.info("VM free:{}", byteCountToDisplaySize(Runtime.getRuntime().freeMemory()));
    log.info("Cache stats:{}", cache.stats());
    int top = 10;
    log.info("Ohc hottestKeys({}):{}", top, hottestKeys(top));
  }

  private static String byteCountToDisplaySize(long l) {
    if (l > ONE_MB) {
      return l / ONE_MB + " MB";
    }
    if (l > 1024) {
      return l / 1024 + " KB";
    }
    return Long.toString(l);
  }

  public void remove(String key) {
    cache.remove(key);
  }

  public void clear() {
    cache.clear();
  }

  public Set<String> hottestKeys(int top) {
    Set<String> keys = new HashSet<>();
    try (CloseableIterator<String> hotKeyIterator = cache.hotKeyIterator(top)) {
      while (hotKeyIterator.hasNext()) {
        keys.add(hotKeyIterator.next());
      }
    } catch (IOException e) {
      log.error("", e);
    }
    return keys;
  }

  public int[] hitMissCount(List<String> keys) {
    if (keys == null || keys.size() == 0) {
      return new int[] {0, 0};
    }
    AtomicInteger hit = new AtomicInteger();
    AtomicInteger miss = new AtomicInteger();
    keys.parallelStream()
        .forEach(
            k -> {
              if (cache.containsKey(k)) {
                hit.getAndIncrement();
              } else {
                miss.getAndIncrement();
              }
            });
    return new int[] {hit.get(), miss.get()};
  }

  public int[] hitMissCount(String[] keys) {
    if (keys == null || keys.length == 0) {
      return new int[] {0, 0};
    }
    AtomicInteger hit = new AtomicInteger();
    AtomicInteger miss = new AtomicInteger();
    Arrays.stream(keys)
        .parallel()
        .forEach(
            k -> {
              if (cache.containsKey(k)) {
                hit.getAndIncrement();
              } else {
                miss.getAndIncrement();
              }
            });
    return new int[] {hit.get(), miss.get()};
  }
}
