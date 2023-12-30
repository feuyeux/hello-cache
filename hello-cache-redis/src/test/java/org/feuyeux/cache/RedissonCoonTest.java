package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.redis.RedissonCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class RedissonCoonTest {

  private RedissonCoon redissonCoon = new RedissonCoon();

  @Test
  public void redisson() {
    String KEY = "X1";
    redissonCoon.put(KEY, "1");
    String x = redissonCoon.get(KEY);
    log.info(x);
    assertEquals("1", x);
  }
}
