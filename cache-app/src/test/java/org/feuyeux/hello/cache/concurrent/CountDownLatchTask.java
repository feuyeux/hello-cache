package org.feuyeux.hello.cache.concurrent;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.CaffeineCoon;

@Slf4j
record CountDownLatchTask(CaffeineCoon<Integer, Integer> caffeineCoon,
                          int name, CountDownLatch startSignal,
                          CountDownLatch doneSignal) implements Runnable {

  @Override
  public void run() {
    try {
      log.info(name + " 等待进入...");
      startSignal.await();
      doWork();
      doneSignal.countDown();
      log.info(name + " 等待离开...");
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  void doWork() throws InterruptedException {
    log.info(name + " 进来了 > 开始工作");
    log.info("{}", caffeineCoon.get(name));
    log.info(name + " 完成工作 <");
  }
}
