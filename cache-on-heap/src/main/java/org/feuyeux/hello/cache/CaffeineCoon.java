package org.feuyeux.hello.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//https://github.com/ben-manes/caffeine
@Slf4j
@Service
public class CaffeineCoon<K, V> {

  private LoadingCache<K, V> cache;
  @Setter
  private int maximumSize = 100;
  private final int top = 10;

  @PostConstruct
  public void init() {
    cache = Caffeine.newBuilder().recordStats()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(maximumSize)
        .build(this::readThrough);
  }

  private V readThrough(K key) {
    //TODO
    return null;
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.get(key, k -> {
      //TODO
      return null;
    });
  }

  public void remove(K key) {
    cache.asMap().remove(key);
  }

  public void clear() {
    cache.asMap().clear();
  }

  public void cleanUp() {
    cache.cleanUp();
  }

  public void stats() {
    CacheStats stats = cache.stats();
    log.info("Caffeine stats:{}", stats);
    log.info("Caffeine hottestKeys({}):{}", top, hottestKeys(top));
  }

  public Set<K> hottestKeys(int top) {
    Set<K> keys = new HashSet<>();
    cache.policy().eviction().ifPresent(eviction -> {
      keys.addAll(eviction.hottest(top).keySet());
      // keys.addAll(eviction.coldest(top).keySet());
    });
    return keys;
  }
}
