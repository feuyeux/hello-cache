package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.CaffeineCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class CaffeineCoonTest {

  @Test
  public void test() {
    CaffeineCoon<String, String> caffeineCoon;
    caffeineCoon = new CaffeineCoon<>();
    String KEY = "BS1";
    caffeineCoon.put(KEY, "1");
    String x = caffeineCoon.get(KEY);
    log.info(x);
    assertEquals("1", x);
  }

  @Test
  public void testBytes() {
    CaffeineCoon<byte[], byte[]> bytesCaffeineCoon;
    bytesCaffeineCoon = new CaffeineCoon<>();
    String s = UUID.randomUUID().toString();
    byte[] KEY = s.getBytes(StandardCharsets.UTF_8);
    bytesCaffeineCoon.put(KEY, "わかった".getBytes(StandardCharsets.UTF_8));
    byte[] bytes = bytesCaffeineCoon.get(KEY);
    String r = new String(bytes, StandardCharsets.UTF_8);
    log.info("{}", r);
    assertEquals("わかった", r);
  }

  @Test
  public void testObject() {
    CaffeineCoon<String, MyTestEntity> oCaffeineCoon;
    oCaffeineCoon = new CaffeineCoon<>();
    String s = UUID.randomUUID().toString();
    MyTestEntity t1 = MyTestEntity.builder().id(System.currentTimeMillis()).value(s).build();
    oCaffeineCoon.put(s, t1);
    MyTestEntity t2 = oCaffeineCoon.get(s);
    log.info("t1:{},t2:{}", t1, t2);
    assertEquals(t1, t2);
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
