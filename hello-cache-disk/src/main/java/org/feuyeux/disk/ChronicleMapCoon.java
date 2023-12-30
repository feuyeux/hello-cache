package org.feuyeux.disk;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMap;

/**
 * @author feuyeux
 */
// https://github.com/OpenHFT/Chronicle-Queue#quick-start
@Slf4j
public class ChronicleMapCoon<K, V> {

  private ChronicleMap<K, V> cache;
  private static final String CHRONIC_PATH = System.getProperty("user.home") + " /chronicle";

  public ChronicleMapCoon(Class<K> keyClass, Class<V> valueClass) {
    this(keyClass, valueClass, CHRONIC_PATH);
  }

  public ChronicleMapCoon(Class<K> keyClass, Class<V> valueClass, String persistentPath) {
    try {
      final File persistentFile = initPersistentPath(persistentPath);
      switch (valueClass.getSimpleName()) {
        case "String" -> cache =
            ChronicleMap.of(keyClass, valueClass)
                .name("cache-map")
                .entries(50)
                .averageKeySize(100)
                .averageValueSize(100)
                .createPersistedTo(persistentFile);
        case "Integer" -> cache =
            ChronicleMap.of(keyClass, valueClass)
                .name("cache-map")
                .entries(50)
                .averageKeySize(100)
                .createPersistedTo(persistentFile);
      }
    } catch (IOException e) {
      log.error("ChronicleMap initializing error", e);
    }
  }

  private static File initPersistentPath(String persistentPath) {
    log.info("ChronicleMap initializing...");
    log.info("FILEPATH：{}", persistentPath);
    File file = new File(persistentPath);
    if (!file.exists()) {
      boolean mkdir = file.mkdirs();
      log.debug("mkdir?{}", mkdir);
    }
    return new File(persistentPath + "/db");
  }

  public void destroy() {
    if (cache != null) {
      cache.close();
    }
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.get(key);
  }

  public void remove(K key) {
    cache.remove(key);
  }

  public void stats() {
    /*ChronicleMap.SegmentStats[] segmentStats = cache.segmentStats();
    if (segmentStats != null && segmentStats.length > 0) {
        Arrays.stream(segmentStats).iterator().forEachRemaining(i -> log.info("Chronic stats:{},{},{},{}",
                i.usesSelfDescribingMessage(),
                i.tiers(),
                i.sizeInBytes(),
                i.usedBytes()));
    }*/
    log.info("Chronic stats, size:{},offHeap:{}", cache.size(), cache.offHeapMemoryUsed());
  }
}
