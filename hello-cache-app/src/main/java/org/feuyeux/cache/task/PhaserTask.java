package org.feuyeux.cache.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.CaffeineCoon;

@Slf4j
public record PhaserTask(
    Integer sequence, Phaser phaser, CaffeineCoon<Integer, Integer> caffeineCoon)
    implements Callable<Integer> {

  @Override
  public Integer call() {
    try {
      log.info(sequence + " waiting...");
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
    log.info(sequence + " working");
    Integer result = caffeineCoon.get(sequence);
    log.info(sequence + " done");
    return result;
  }
}
