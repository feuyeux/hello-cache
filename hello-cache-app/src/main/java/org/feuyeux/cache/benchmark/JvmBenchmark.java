package org.feuyeux.cache.benchmark;

import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.cache.benchmark.coon.*;
import org.openjdk.jmh.annotations.*;
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
@BenchmarkMode({Throughput, AverageTime})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 2)
@Threads(4)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Slf4j
public class JvmBenchmark {

  private static final int SIZE = (2 << 16);
  private static final int MASK = SIZE - 1;
  private static final int ITEMS = SIZE / 3;

  @Param({"CHM", "Cache2k", "Caffeine", "Guava"})
  String type;

  BasicCache<Integer, Integer> cache;
  Integer[] values;

  @State(Scope.Thread)
  public static class ThreadState {

    static final Random RANDOM = new Random();
    int index = RANDOM.nextInt();
  }

  @Setup
  public void setup() {
    values = new Integer[SIZE];
    switch (type) {
      case "CHM" -> cache = new ConcurrentMapCache<>(new ConcurrentHashMap<>(2 * SIZE));
      case "Caffeine" -> cache = new CaffeineCache<>(2 * SIZE);
      case "Guava" -> cache = new GuavaCache<>(2 * SIZE);
      case "Cache2k" -> cache = new Cache2kCache<>(2 * SIZE);
      default -> throw new AssertionError("Unknown type: " + type);
    }
    // Enforce full initialization of internal structures
    for (int i = 0; i < 2 * SIZE; i++) {
      cache.put(i, i);
    }
    cache.clear();
    cache.cleanUp();

    // Populate using a realistic access distribution
    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      values[i] = generator.nextValue().intValue();
      cache.put(values[i], i);
    }
  }

  @TearDown(Level.Iteration)
  public void tearDown() {
    cache.cleanUp();
  }

  @Benchmark
  @Group("RO")
  @GroupThreads(8)
  public Integer readOnly(ThreadState threadState) {
    Integer key = values[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("WO")
  @GroupThreads(8)
  public void writeOnly(ThreadState threadState) {
    Integer key = values[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(6)
  public Integer readwriteGet(ThreadState threadState) {
    Integer key = values[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(2)
  public void readwritePut(ThreadState threadState) {
    Integer key = values[threadState.index++ & MASK];
    cache.put(key, key);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder()
            .include(JvmBenchmark.class.getSimpleName())
            .param("computeType", "Cache2k", "Caffeine", "Guava", "CHM")
            .result("jvm_benchmark.json")
            .resultFormat(ResultFormatType.JSON)
            .build();
    new Runner(opt).run();
  }
}
