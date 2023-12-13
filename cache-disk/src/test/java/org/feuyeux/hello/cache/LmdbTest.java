package org.feuyeux.hello.cache;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class LmdbTest {

  Random random = new Random();
  private final ChronicleMapCoon<String, Integer> chronicleMapCoon = new ChronicleMapCoon(
      String.class, Integer.class);
  @Autowired
  private LmdbCoon<Integer> lmdbCoon;

  @BeforeEach
  public void init() {
    chronicleMapCoon.init();
  }

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
