package org.feuyeux.cache.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.CaffeineCoon;

@Slf4j
public record SemaphoreTask(
    Integer sequence, Semaphore semaphore, CaffeineCoon<Integer, Integer> caffeineCoon)
    implements Callable<Integer> {

  @Override
  public Integer call() {
    try {
      log.info(sequence + " waiting...");
      semaphore.acquire();
      Integer result = doWork();
      semaphore.release();
      return result;
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
