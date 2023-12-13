package org.feuyeux.hello.cache;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.coon.AsyncCache;
import org.feuyeux.hello.cache.coon.JedisCache;
import org.feuyeux.hello.cache.coon.LettuceCache;
import org.feuyeux.hello.cache.coon.RedissonCache;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import site.ycsb.generator.NumberGenerator;
import site.ycsb.generator.ScrambledZipfianGenerator;

@State(Scope.Group)
@BenchmarkMode(Throughput)
@Slf4j
public class RemoteBenchmark {

  @Param({"Redisson", "Lettuce", "Jedis"})
  String type;
  AsyncCache<String, String> cache;
  private static String[] ints;
  private static final int SIZE = 10_000;
  private static final int MASK = SIZE - 1;
  private static final int ITEMS = SIZE / 3;

  @State(Scope.Thread)
  public static class ThreadState {

    static final Random random = new Random();
    int index = random.nextInt();
  }

  @Setup
  public void setup() throws Exception {
    ints = new String[SIZE];
    switch (type) {
      case "Redisson" -> cache = new RedissonCache();
      case "Lettuce_" -> cache = new LettuceCache();
      case "Jedis" -> cache = new JedisCache();
      default -> throw new AssertionError("Unknown type: " + type);
    }

    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      ints[i] = generator.nextValue().toString();
      cache.putAsync(ints[i], String.valueOf(i));
    }
    log.info("[{}] Tear up done.", type);
  }

  @TearDown(Level.Iteration)
  public void tearDown() {
    log.info("[{}] Tear down.", type);
  }

  @Benchmark
  @Group("Sync")
  @GroupThreads(6)
  public void read(ThreadState threadState, Blackhole blackhole) {
    String key = type + "_" + ints[threadState.index++ & MASK];
    blackhole.consume(cache.get(key));
  }

  @Benchmark
  @Group("Sync")
  @GroupThreads(2)
  public void write(ThreadState threadState) {
    String key = type + "_" + ints[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("Async")
  @GroupThreads(6)
  public void readAsync(ThreadState threadState, Blackhole blackhole) {
    String key = type + "_" + ints[threadState.index++ & MASK];
    CompletionStage<String> future = cache.getAsync(key);
    future.whenComplete((s, throwable) -> blackhole.consume(s));
  }

  @Benchmark
  @Group("Async")
  @GroupThreads(2)
  public void writeAsync(ThreadState threadState, Blackhole blackhole) throws Exception {
    String key = type + "_" + ints[threadState.index++ & MASK];
    CompletionStage stage = cache.putAsync(key, key);
    stage.whenComplete((o, o2) -> blackhole.consume(o));
  }
}
