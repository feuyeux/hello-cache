package org.feuyeux.hello.cache.concurrent;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.CaffeineCoon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * CountDownLatch CyclicBarrier Phaser Semaphore
 */
@SpringBootTest
@Slf4j
public class ConcurrentTest {

  private final int times = 4;
  private final int parties = 4;
  private final ExecutorService exec = Executors.newFixedThreadPool(times);

  @Autowired
  private CaffeineCoon<Integer, Integer> caffeineCoon;

  @Autowired
  private ApplicationContext applicationContext;

  @BeforeEach
  public void init() {
    Environment environment = applicationContext.getEnvironment();
    log.info("app:{}", environment.getProperty("app.name"));
  }

  @Test
  public void semaphoreTest() throws InterruptedException {
    final Semaphore semaphore = new Semaphore(times / 2);
    ArrayList<Future<Integer>> taskList = new ArrayList<>();
    for (int i = 0; i < times * 2; i++) {
      Future<Integer> f = exec.submit(new SemaphoreTask(i, semaphore, caffeineCoon));
      taskList.add(f);
    }
    exec.shutdown();
    for (int i = 0; i < taskList.size(); i++) {
      try {
        Integer sequence = taskList.get(i).get();
        log.info(i + " 离开: " + sequence);
      } catch (ExecutionException e) {
        log.error("", e);
      }
    }
  }

  @Test
  public void phaserTest() throws InterruptedException {
    int repeats = 3;
    final Phaser phaser = new Phaser() {
      @Override
      protected boolean onAdvance(int phase, int registeredParties) {
        log.info("PHASE[" + phase + "],Parties[" + registeredParties + "]");
        return phase + 1 >= repeats || registeredParties == 0;
      }
    };
    ArrayList<Future<Integer>> taskList = new ArrayList<>();
    for (int i = 0; i < parties; i++) {
      // parties+1
      phaser.register();
      Future<Integer> f = exec.submit(new PhaserTask(i, phaser, caffeineCoon));
      taskList.add(f);
    }
    while (!phaser.isTerminated()) {
      TimeUnit.SECONDS.sleep(1);
      phaser.arriveAndDeregister();
    }
    exec.shutdown();

    for (int i = 0; i < times; i++) {
      try {
        Integer sequence = taskList.get(i).get();
        log.info(i + " 离开: " + sequence);
      } catch (ExecutionException e) {
        log.error("", e);
      }
    }
  }

  @Test
  public void cyclicBarrierTest() throws InterruptedException {
    final CyclicBarrier barrier = new CyclicBarrier(
        times,
        () -> {
          // barrierAction
          try {
            TimeUnit.MILLISECONDS.sleep(1000);
            log.info("开门， 大家一起进入");
          } catch (InterruptedException e) {
            log.error("", e);
          }
        });
    ArrayList<Future<Integer>> taskList = new ArrayList<>();
    for (int i = 0; i < times; i++) {
      Future<Integer> f = exec.submit(new CyclicBarrierTask(caffeineCoon, i, barrier));
      taskList.add(f);
    }
    exec.shutdown();
    for (int i = 0; i < times; i++) {
      try {
        Integer sequence = taskList.get(i).get();
        log.info(i + " 离开: " + sequence);
      } catch (ExecutionException e) {
        log.error("", e);
      }
    }
  }

  @Test
  public void countDownLatchTest() throws InterruptedException {
    final CountDownLatch startGate = new CountDownLatch(1);
    final CountDownLatch endGate = new CountDownLatch(times);

    for (int i = 0; i < times; i++) {
      exec.execute(new CountDownLatchTask(caffeineCoon, i, startGate, endGate));
    }
    exec.shutdown();
    startGate.countDown();
    endGate.await();
  }
}
