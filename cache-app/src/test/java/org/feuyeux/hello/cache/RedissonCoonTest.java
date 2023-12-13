package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class RedissonCoonTest {

  @Autowired
  private RedissonCoon redissonCoon;

  @Test
  public void redisson() {
    String KEY = "X1";
    redissonCoon.put(KEY, "1");
    String x = redissonCoon.get(KEY);
    log.info(x);
    assertEquals("1", x);
  }
}
