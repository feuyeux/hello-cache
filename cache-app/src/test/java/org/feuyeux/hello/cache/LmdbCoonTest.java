package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class LmdbCoonTest {

  @Autowired
  private LmdbCoon<String> lmdbCoon;
  @Autowired
  private LmdbAgronaCoon agronaCoon;

  @Test
  public void lmdb() {
    lmdbCoon.put("X", "1");
    String x = lmdbCoon.get("X");
    log.info(x);
    assertEquals("1", x);
  }

  @Test
  public void testBytes() {
    byte[] KEY = "XX3".getBytes(StandardCharsets.UTF_8);
    lmdbCoon.putBytes(KEY, "わかった".getBytes(StandardCharsets.UTF_8));
    byte[] bytes = lmdbCoon.getBytes(KEY);
    String x = new String(bytes, StandardCharsets.UTF_8);
    log.info(x);
    assertEquals("わかった", x);
  }

  @Test
  public void agrona() {
    agronaCoon.put("X", "1");
    String x = agronaCoon.get("X");
    log.info(x);
    assertEquals("1", x);
  }
}
