package org.feuyeux.hello.cache.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.CaffeineCoon;

@Slf4j
record CyclicBarrierTask(CaffeineCoon<Integer, Integer> caffeineCoon,
                         Integer sequence,
                         CyclicBarrier barrier) implements Callable<Integer> {

  @Override
  public Integer call() {
    try {
      log.info(sequence + " 等待进入...");
      barrier.await();
      return doWork();
    } catch (InterruptedException | BrokenBarrierException e) {
      log.error("", e);
      return -1;
    }
  }

  Integer doWork() throws InterruptedException {
    log.info(sequence + " 进来了 > 开始工作");
    Integer result = caffeineCoon.get(sequence);
    log.info("{}", result);
    log.info(sequence + " 完成工作 <");
    return result;
  }
}
