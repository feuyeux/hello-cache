package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.GuavaCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class GuavaCoonTest {

  private final GuavaCoon<String, String> guavaCoon = new GuavaCoon<>();

  @Test
  public void guava() {
    String k = UUID.randomUUID().toString();
    String value = "20220830";
    guavaCoon.put(k, value);
    String x = guavaCoon.get(k);
    log.info("{}:{}", k, x);
    assertEquals(value, x);
  }
}
