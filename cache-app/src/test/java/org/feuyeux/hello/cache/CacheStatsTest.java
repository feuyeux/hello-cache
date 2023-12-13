package org.feuyeux.hello.cache;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class CacheStatsTest {

  @Autowired
  private Cache2kCoon<Integer, Integer> cache2kCoon;
  @Autowired
  private CaffeineCoon<Integer, Integer> caffeineCoon;
  private static final Random random = new Random();

  @BeforeEach
  public void setup() {
    for (int i = 0; i < 10; i++) {
      int value = random.nextInt();
      cache2kCoon.put(i, value);
      caffeineCoon.put(i, value);
    }
    cache2kCoon.get(1);
    caffeineCoon.get(1);
    cache2kCoon.get(11);
    caffeineCoon.get(11);
  }

  @Test
  public void testCache2kStats() {
    cache2kCoon.stats();
  }

  @Test
  public void testCaffeineStats() {
    caffeineCoon.stats();
  }
}
