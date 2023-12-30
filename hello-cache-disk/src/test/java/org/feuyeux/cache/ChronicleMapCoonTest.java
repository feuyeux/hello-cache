package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.disk.ChronicleMapCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class ChronicleMapCoonTest {

  @Test
  public void test() {
    ChronicleMapCoon<String, String> coon =
        new ChronicleMapCoon<>(
            String.class, String.class, "/tmp/chronic" + System.currentTimeMillis());
    String KEY = UUID.randomUUID().toString();
    String value = "PEACE&LOVE";
    coon.put(KEY, value);
    String x = coon.get(KEY);
    log.info(x);
    assertEquals(value, x);
  }
}
