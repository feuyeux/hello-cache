package org.feuyeux.cache.task;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.memory.CaffeineCoon;

@Slf4j
public record CountDownLatchTask(
    CaffeineCoon<Integer, Integer> caffeineCoon,
    int name,
    CountDownLatch startSignal,
    CountDownLatch doneSignal)
    implements Runnable {

  @Override
  public void run() {
    try {
      log.info(name + " waiting...");
      startSignal.await();
      doWork();
      doneSignal.countDown();
      log.info(name + " leaving...");
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  void doWork() throws InterruptedException {
    log.info(name + " working");
    log.info("{}", caffeineCoon.get(name));
    log.info(name + " done");
  }
}
