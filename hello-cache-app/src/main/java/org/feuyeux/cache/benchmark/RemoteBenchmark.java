package org.feuyeux.cache.benchmark;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.cache.benchmark.coon.AsyncCache;
import org.feuyeux.cache.benchmark.coon.JedisCache;
import org.feuyeux.cache.benchmark.coon.LettuceCache;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import site.ycsb.generator.NumberGenerator;
import site.ycsb.generator.ScrambledZipfianGenerator;

/**
 * @author feuyeux
 */
@State(Scope.Group)
@BenchmarkMode({Throughput})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 2)
@Threads(4)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Slf4j
public class RemoteBenchmark {

  @Param({
      /*"Redisson", */
    "Lettuce", "Jedis"
  })
  String type;

  AsyncCache<String, String> cache;
  private static String[] values;
  private static final int SIZE = 10_000;
  private static final int MASK = SIZE - 1;
  private static final int ITEMS = SIZE / 3;

  @State(Scope.Thread)
  public static class ThreadState {
    int index = new Random().nextInt();
  }

  @Setup
  public void setup() throws Exception {
    values = new String[SIZE];
    switch (type) {
        /*case "Redisson" -> cache = new RedissonCache();*/
      case "Lettuce" -> cache = new LettuceCache();
      case "Jedis" -> cache = new JedisCache();
      default -> throw new AssertionError("Unknown type: " + type);
    }

    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      values[i] = generator.nextValue().toString();
      cache.putAsync(values[i], String.valueOf(i));
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
    String key = type + "_" + values[threadState.index++ & MASK];
    blackhole.consume(cache.get(key));
  }

  @Benchmark
  @Group("Sync")
  @GroupThreads(2)
  public void write(ThreadState threadState) {
    String key = type + "_" + values[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("Async")
  @GroupThreads(6)
  public void readAsync(ThreadState threadState, Blackhole blackhole) {
    String key = type + "_" + values[threadState.index++ & MASK];
    CompletionStage<String> future = cache.getAsync(key);
    future.whenComplete((s, throwable) -> blackhole.consume(s));
  }

  @Benchmark
  @Group("Async")
  @GroupThreads(2)
  public void writeAsync(ThreadState threadState, Blackhole blackhole) throws Exception {
    String key = type + "_" + values[threadState.index++ & MASK];
    CompletionStage<String> stage = cache.putAsync(key, key);
    stage.whenComplete((o, o2) -> blackhole.consume(o));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder()
            .include(RemoteBenchmark.class.getSimpleName())
            .param("computeType", "Jedis", "Lettuce")
            .result("redis_benchmark.json")
            .resultFormat(ResultFormatType.JSON)
            .build();
    new Runner(opt).run();
  }
}
