package org.feuyeux.hello.cache.coon;


public interface BasicCache<K, V> {

  /**
   * Returns the value stored in the cache, or null if not present.
   */
  V get(K key);

  /**
   * Stores the value into the cache, replacing an existing mapping if present.
   */
  void put(K key, V value);

  default void put(K key, V value, long timeout) {
  }

  default void put(K key, V value, boolean isLoad) {
    put(key, value);
  }

  /**
   * Removes the entry from the cache, if present.
   */
  void remove(K key);

  /**
   * Invalidates all entries from the cache.
   */
  void clear();

  void stats();

  /**
   * Performs any pending maintenance operations needed by the cache.
   */
  default void cleanUp() {
  }

  default void close() {
  }
}
