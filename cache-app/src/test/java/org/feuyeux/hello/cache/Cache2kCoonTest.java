package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class Cache2kCoonTest {

  @Autowired
  private Cache2kCoon<String, String> cache2kCoon;

  @BeforeEach
  public void setup() {
    cache2kCoon.init();
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
