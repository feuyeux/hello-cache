package org.feuyeux.hello.cache.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.CaffeineCoon;

@Slf4j
record SemaphoreTask(
    Integer sequence,
    Semaphore semaphore,
    CaffeineCoon<Integer, Integer> caffeineCoon
) implements Callable<Integer> {

  @Override
  public Integer call() {
    try {
      log.info(sequence + " 等待进入...");
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
    log.info(sequence + " 进来了 > 开始工作");
    Integer result = caffeineCoon.get(sequence);
    log.info(sequence + " 完成工作 <");
    return result;
  }
}
