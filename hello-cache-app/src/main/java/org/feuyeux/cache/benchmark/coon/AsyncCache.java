package org.feuyeux.cache.benchmark.coon;

import java.util.concurrent.CompletionStage;

public interface AsyncCache<K, V> extends BasicCache<K, V> {

  /** Returns the value stored in the cache, or null if not present. */
  CompletionStage<V> getAsync(K key);

  /** Stores the value into the cache, replacing an existing mapping if present. */
  CompletionStage<String> putAsync(K key, V value) throws Exception;

  default void stats() {}
}
