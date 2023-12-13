package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.pojo.MyTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class CaffeineCoonTest {

  @Autowired
  private CaffeineCoon<String, String> caffeineCoon;
  @Autowired
  private CaffeineCoon<byte[], byte[]> bytesCaffeineCoon;
  @Autowired
  private CaffeineCoon<String, MyTestEntity> oCaffeineCoon;

  @Test
  public void test() {
    String KEY = "BS1";
    caffeineCoon.put(KEY, "1");
    String x = caffeineCoon.get(KEY);
    log.info(x);
    assertEquals("1", x);
  }

  @Test
  public void testBytes() {
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
    String s = UUID.randomUUID().toString();
    MyTestEntity t1 = MyTestEntity.builder().id(System.currentTimeMillis()).value(s).build();
    oCaffeineCoon.put(s, t1);
    MyTestEntity t2 = oCaffeineCoon.get(s);
    log.info("t1:{},t2:{}", t1, t2);
    assertEquals(t1, t2);
  }
}
