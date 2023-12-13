package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.pojo.MyTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class OhcCoonTest {

  @Autowired
  private OhcCoon<String> ohcCoon;
  @Autowired
  private OhcCoon<byte[]> ohcBytesCoon;
  @Autowired
  private OhcCoon<MyTestEntity> ohcEntityCoon;

  @Test
  public void offHeapCache() {
    String KEY = UUID.randomUUID().toString();
    String value = "1";
    ohcCoon.put(KEY, value);
    String x = ohcCoon.get(KEY);
    log.info(x);
    assertEquals(value, x);
  }

  @Test
  public void testTimeout() throws InterruptedException {
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
    ohcBytesCoon.put("XX3", "わかった".getBytes(StandardCharsets.UTF_8));
    byte[] bytes = ohcBytesCoon.get("XX3");
    String x = new String(bytes, StandardCharsets.UTF_8);
    log.info(x);
    assertEquals("わかった", x);
  }

  @Test
  public void testObject() {
    List<String> keys = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      String key = UUID.randomUUID().toString();
      keys.add(key);
    }
    for (int i = 0; i < 10; i++) {
      String key = keys.get(i);
      MyTestEntity entity = MyTestEntity.builder().id(System.currentTimeMillis()).value(key)
          .build();
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
}
