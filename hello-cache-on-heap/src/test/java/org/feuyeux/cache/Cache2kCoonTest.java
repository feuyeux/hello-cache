package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.Cache2kCoon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class Cache2kCoonTest {

  private Cache2kCoon<String, String> cache2kCoon;

  @BeforeEach
  public void setup() {
    cache2kCoon = new Cache2kCoon<>();
  }

  @Test
  public void cache2k() {
    String KEY = "A1";
    cache2kCoon.put(KEY, "1");
    String x = cache2kCoon.get(KEY);
    log.info(x);
    assertEquals("1", x);
  }
}
