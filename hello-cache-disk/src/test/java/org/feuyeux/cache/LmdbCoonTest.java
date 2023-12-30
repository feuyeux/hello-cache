package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.disk.LmdbAgronaCoon;
import org.feuyeux.disk.LmdbCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class LmdbCoonTest {

  private final LmdbCoon<String> lmdbCoon = new LmdbCoon<>();

  private final LmdbAgronaCoon agronaCoon = new LmdbAgronaCoon();

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
