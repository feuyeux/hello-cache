package org.feuyeux.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.disk.RocksdbCoon;
import org.junit.jupiter.api.Test;

@Slf4j
public class RocksCoonTest {

  private final RocksdbCoon<String> rocksdbCoon =
      new RocksdbCoon<>("/tmp/rocks_" + System.currentTimeMillis());

  @Test
  public void test() {
    rocksdbCoon.put("XX3", "わかった");
    String x = rocksdbCoon.get("XX3");
    log.info(x);
    assertEquals("わかった", x);
  }
}
