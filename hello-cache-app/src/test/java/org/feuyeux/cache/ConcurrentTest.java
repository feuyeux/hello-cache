package org.feuyeux.cache;

import java.util.ArrayList;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.cache.task.CountDownLatchTask;
import org.feuyeux.cache.task.CyclicBarrierTask;
import org.feuyeux.cache.task.PhaserTask;
import org.feuyeux.cache.task.SemaphoreTask;
import org.feuyeux.memory.CaffeineCoon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** CountDownLatch CyclicBarrier Phaser Semaphore */
@Slf4j
public class ConcurrentTest {

  private final int times = 4;
  private final ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();

  private CaffeineCoon<Integer, Integer> caffeineCoon;

  @BeforeEach
  public void init() {
    caffeineCoon = new CaffeineCoon<>();
    for (int i = 0; i < times * 2; i++) {
      caffeineCoon.put(i, i);
    }
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
        log.info(i + " leave: " + sequence);
      } catch (ExecutionException e) {
        log.error("", e);
      }
    }
  }

  @Test
  public void phaserTest() throws InterruptedException {
    int repeats = 3;
    final Phaser phaser =
        new Phaser() {
          @Override
          protected boolean onAdvance(int phase, int registeredParties) {
            log.info("PHASE[" + phase + "],Parties[" + registeredParties + "]");
            return phase + 1 >= repeats || registeredParties == 0;
          }
        };
    ArrayList<Future<Integer>> taskList = new ArrayList<>();
    for (int i = 0; i < times; i++) {
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
    final CyclicBarrier barrier =
        new CyclicBarrier(
            times,
            () -> {
              // barrierAction
              try {
                TimeUnit.MILLISECONDS.sleep(1000);
                log.info("together");
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
        log.info(i + " leave: " + sequence);
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

  @Test
  public void testCompleteService() throws Exception {
    int numTasks = 4;
    ExecutorService executor = Executors.newFixedThreadPool(numTasks);
    CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

    for (int i = 0; i < numTasks; i++) {
      final int taskId = i;
      completionService.submit(
          () -> {
            int sleepTime = (int) (Math.random() * 10);
            TimeUnit.SECONDS.sleep(sleepTime);
            log.info("Task " + taskId + " completed after " + sleepTime + " seconds");
            return "TASK-" + taskId;
          });
    }
    Future<String> completedTask = completionService.take();
    log.info("First completed task: " + completedTask.get());
    while (completionService.poll() != null) {
      // Poll the remaining completed tasks
      completedTask = completionService.poll();
      completedTask.cancel(true);
    }
    // Shutdown the executor
    executor.shutdown();
  }
}
