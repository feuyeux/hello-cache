package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class GuavaCoonTest {

  @Autowired
  private GuavaCoon<String, String> guavaCoon;

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
