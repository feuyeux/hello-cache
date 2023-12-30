package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.redis.JedisCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class JedisCoonTest {

  private JedisCoon jedisCoon = new JedisCoon();

  @Test
  public void jedis() {
    String KEY = "X3";
    jedisCoon.put(KEY, "1");
    String x = jedisCoon.get(KEY);
    log.info(x);
    assertEquals("1", x);
  }
}
