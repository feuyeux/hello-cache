package org.feuyeux.hello.cache.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.CaffeineCoon;

@Slf4j
record PhaserTask(
    Integer sequence,
    Phaser phaser,
    CaffeineCoon<Integer, Integer> caffeineCoon
) implements Callable<Integer> {

  @Override
  public Integer call() {
    try {
      log.info(sequence + " 等待进入...");
      int x;
      do {
        x = doWork();
        phaser.arriveAndAwaitAdvance();
        TimeUnit.MICROSECONDS.sleep(100);
      } while (!phaser.isTerminated());
      return x;
    } catch (InterruptedException e) {
      log.error("", e);
      return -1;
    }
  }

  Integer doWork() throws InterruptedException {
    log.info(sequence + " 进来了 > 开始工作");
    Integer result = caffeineCoon.get(sequence);
    log.info(sequence + " 完成工作 <");
    return result;
  }
}
