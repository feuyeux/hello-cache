package org.feuyeux.memory;

import static org.cache2k.extra.micrometer.Cache2kCacheMetrics.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.config.Cache2kConfig;
import org.cache2k.event.CacheEntryEvictedListener;
import org.cache2k.event.CacheEntryExpiredListener;
import org.cache2k.extra.micrometer.MicrometerSupport;
import org.cache2k.operation.CacheControl;
import org.cache2k.operation.CacheStatistics;

/**
 * @author feuyeux
 */
// https://cache2k.org/docs/latest/user-guide.html
@Slf4j
public class Cache2kCoon<K, V> {

  private final Cache<K, V> cache;

  @Setter private long maximumSize = 100;

  public Cache2kCoon() {
    cache =
        Cache2kBuilder.of(new Cache2kConfig<K, V>())
            .entryCapacity(maximumSize)
            .refreshAhead(false)
            .strictEviction(false)
            /*.expireAfterWrite(1, TimeUnit.MINUTES)*/
            /*indicate that the cached values do not expire with time*/
            .eternal(true)
            .enable(MicrometerSupport.class)
            .addAsyncListener(
                (CacheEntryExpiredListener<K, V>)
                    (cache, entry) ->
                        log.info("Entry expired:{},{}", entry.getKey(), entry.getValue()))
            .addAsyncListener(
                (CacheEntryEvictedListener<K, V>)
                    (cache, entry) ->
                        log.info("Entry evicted:{},{}", entry.getKey(), entry.getValue()))
            .build();
    MeterRegistry registry = new SimpleMeterRegistry();
    monitor(registry, cache);
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.get(key);
  }

  public ConcurrentMap<K, V> getAsMap() {
    return cache.asMap();
  }

  public void remove(K key) {
    cache.remove(key);
  }

  public void clear() {
    cache.clear();
  }

  public void stats() {
    CacheControl cacheControl = CacheControl.of(cache);
    CacheStatistics cacheStatistics = cacheControl.sampleStatistics();
    log.info(
        "Cache2k stats: put:{},get:{}({}/{}),evict:{} {}",
        cacheStatistics.getPutCount(),
        cacheStatistics.getGetCount(),
        cacheStatistics.getHitRate(),
        cacheStatistics.getMissCount(),
        cacheStatistics.getEvictedCount(),
        cache.toString());
    hottestKeys(10);
  }

  public Set<K> hottestKeys(int top) {
    CacheControl cacheControl = CacheControl.of(cache);
    return null;
  }
}
