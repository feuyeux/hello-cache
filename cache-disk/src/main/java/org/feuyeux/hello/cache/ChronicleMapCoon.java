package org.feuyeux.hello.cache;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.map.ChronicleMap;

//https://github.com/OpenHFT/Chronicle-Queue#quick-start
@Slf4j
public class ChronicleMapCoon<K, V> {

  private ChronicleMap<K, V> cache;
  private static final String chronicPath = "/tmp/chronic/coon";
  private final boolean isPersisted = true;

  private final Class<K> keyClass;
  private final Class<V> valueClass;

  public ChronicleMapCoon(Class<K> keyClass, Class<V> valueClass) {
    this.keyClass = keyClass;
    this.valueClass = valueClass;
  }

  public void init() {
    if (isPersisted) {
      log.info("ChronicleMap initializing...");
      log.info("FILEPATH：{}", chronicPath);
      File file = new File(chronicPath);
      if (!file.exists()) {
        try {
          boolean mkdir = file.mkdirs();
          log.debug("mkdir?{}", mkdir);
        } catch (Exception e) {
          log.error("", e);
        }
      }
      try {
        cache = ChronicleMap
            .of(keyClass, valueClass)
            .name("country-map")
            .entries(50_000)
            .averageKeySize(1024)
            //.averageValueSize(1024)
            .createPersistedTo(new File(chronicPath + "/db"));
      } catch (Exception e) {
        log.error("", e);
      }
    } else {
      cache = ChronicleMap
          .of(keyClass, valueClass)
          .name("country-map")
          .entries(50_000)
          .averageKeySize(1024)
          .averageValueSize(1024)
          .create();
    }
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
