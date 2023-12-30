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
import org.feuyeux.redis.lettuce.LettuceBytesCoon;
import org.feuyeux.redis.lettuce.LettuceCoon;
import org.feuyeux.redis.lettuce.LettuceObjectCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class LettuceCoonTest {

  private LettuceCoon lettuceCoon = new LettuceCoon();
  private LettuceBytesCoon bytesCoon = new LettuceBytesCoon();
  private LettuceObjectCoon<MyTestEntity> objectCoon = new LettuceObjectCoon<>();

  @Test
  public void test() {
    String s = UUID.randomUUID().toString();
    lettuceCoon.put(s, s);
    String x = lettuceCoon.get(s);
    log.info(x);
    assertEquals(s, x);
  }

  @Test
  public void testBytes() {
    String s = UUID.randomUUID().toString();
    byte[] KEY = s.getBytes(StandardCharsets.UTF_8);
    bytesCoon.put(KEY, "わかった".getBytes(StandardCharsets.UTF_8));
    byte[] bytes = bytesCoon.get(KEY);
    String x1 = new String(bytes, StandardCharsets.UTF_8);
    String x2 = lettuceCoon.get(s);
    log.info(x1);
    log.info(x2);
    assertEquals(x1, x2);
  }

  @Test
  public void testObject() {
    String s = UUID.randomUUID().toString();
    MyTestEntity t1 = MyTestEntity.builder().id(System.currentTimeMillis()).value(s).build();
    objectCoon.put(s, t1);
    MyTestEntity t2 = (MyTestEntity) objectCoon.get(s);
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
