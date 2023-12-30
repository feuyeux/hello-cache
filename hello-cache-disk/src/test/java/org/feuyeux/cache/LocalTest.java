package org.feuyeux.cache;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.disk.ChronicleMapCoon;
import org.feuyeux.disk.LmdbCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class LocalTest {
  Random random = new Random();
  private final ChronicleMapCoon<String, Integer> chronicleMapCoon =
      new ChronicleMapCoon<>(
          String.class, Integer.class, "/tmp/chronic" + System.currentTimeMillis());

  private final LmdbCoon<Integer> lmdbCoon = new LmdbCoon<>();

  @Test
  public void test() {
    for (int i = 0; i < 100; i++) {
      int x = random.nextInt(100);
      int y = random.nextInt(100);
      String k = String.valueOf(x);
      chronicleMapCoon.put(k, y);
      lmdbCoon.put(k, y);
    }
    log.info("");
    lmdbCoon.stats();
    log.info("");
    chronicleMapCoon.stats();
  }
}
