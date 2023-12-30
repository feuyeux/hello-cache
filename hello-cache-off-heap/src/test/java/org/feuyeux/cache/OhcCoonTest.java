package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.OhcCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class OhcCoonTest {

  @Test
  public void offHeapCache() {
    OhcCoon<String> ohcCoon = new OhcCoon<>();
    String KEY = UUID.randomUUID().toString();
    String value = "1";
    ohcCoon.put(KEY, value);
    String x = ohcCoon.get(KEY);
    log.info(x);
    assertEquals(value, x);
  }

  @Test
  public void testTimeout() throws InterruptedException {
    OhcCoon<String> ohcCoon = new OhcCoon<>();
    String KEY = UUID.randomUUID().toString();
    String value = "123321";
    log.info("put:{}", ohcCoon.put(KEY, value, 500));
    String x = ohcCoon.get(KEY);
    log.info("get:{}", x);
    assertEquals(value, x);
    TimeUnit.MILLISECONDS.sleep(500);
    x = ohcCoon.get(KEY);
    log.info("get:{}", x);
    assertNull(x);
  }

  @Test
  public void testBytes() {
    OhcCoon<byte[]> ohcBytesCoon = new OhcCoon<>();
    ohcBytesCoon.put("XX3", "わかった".getBytes(StandardCharsets.UTF_8));
    byte[] bytes = ohcBytesCoon.get("XX3");
    String x = new String(bytes, StandardCharsets.UTF_8);
    log.info(x);
    assertEquals("わかった", x);
  }

  @Test
  public void testObject() {
    OhcCoon<MyTestEntity> ohcEntityCoon = new OhcCoon<>();
    List<String> keys = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      String key = UUID.randomUUID().toString();
      keys.add(key);
    }
    for (int i = 0; i < 10; i++) {
      String key = keys.get(i);
      MyTestEntity entity =
          MyTestEntity.builder().id(System.currentTimeMillis()).value(key).build();
      ohcEntityCoon.put(key, entity);
    }
    String key = keys.get(5);
    assertEquals(key, ohcEntityCoon.get(key).getValue());

    int[] hitMissCount = ohcEntityCoon.hitMissCount(keys);
    int hit = hitMissCount[0];
    int miss = hitMissCount[1];
    log.info("hit:{},miss:{}", hit, miss);
    assertEquals(10, hit);
    assertEquals(90, miss);
  }

  @Data
  @Builder
  @EqualsAndHashCode
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MyTestEntity implements Serializable {

    private long id;
    private String value;
  }
}
