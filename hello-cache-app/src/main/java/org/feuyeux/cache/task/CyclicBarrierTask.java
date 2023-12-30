package org.feuyeux.cache.task;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.CaffeineCoon;

@Slf4j
public record CyclicBarrierTask(
    CaffeineCoon<Integer, Integer> caffeineCoon, Integer sequence, CyclicBarrier barrier)
    implements Callable<Integer> {

  @Override
  public Integer call() {
    try {
      log.info(sequence + " waiting...");
      barrier.await();
      return doWork();
    } catch (InterruptedException | BrokenBarrierException e) {
      log.error("", e);
      return -1;
    }
  }

  Integer doWork() throws InterruptedException {
    log.info(sequence + " working");
    Integer result = caffeineCoon.get(sequence);
    log.info("{}", result);
    log.info(sequence + " done");
    return result;
  }
}
