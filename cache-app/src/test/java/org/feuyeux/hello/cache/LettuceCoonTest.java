package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.lettuce.LettuceBytesCoon;
import org.feuyeux.hello.cache.lettuce.LettuceCoon;
import org.feuyeux.hello.cache.lettuce.LettuceObjectCoon;
import org.feuyeux.hello.cache.pojo.MyTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class LettuceCoonTest {

  @Autowired
  private LettuceCoon lettuceCoon;
  @Autowired
  private LettuceBytesCoon bytesCoon;
  @Autowired
  private LettuceObjectCoon<MyTestEntity> objectCoon;

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
}

