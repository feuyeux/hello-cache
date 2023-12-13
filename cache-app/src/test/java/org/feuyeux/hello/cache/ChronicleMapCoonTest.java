package org.feuyeux.hello.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ChronicleMapCoonTest {

  @Autowired
  private ChronicleMapCoon<String, String> coon;

  @Test
  public void offHeapCache() {
    String KEY = UUID.randomUUID().toString();
    String value = "1";
    coon.put(KEY, value);
    String x = coon.get(KEY);
    log.info(x);
    assertEquals(value, x);
  }
}
