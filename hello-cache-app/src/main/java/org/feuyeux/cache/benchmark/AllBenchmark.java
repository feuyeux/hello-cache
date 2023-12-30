package org.feuyeux.cache.benchmark;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.cache.benchmark.coon.BasicCache;
import org.feuyeux.cache.benchmark.coon.Cache2kCache;
import org.feuyeux.cache.benchmark.coon.CaffeineCache;
import org.feuyeux.cache.benchmark.coon.GroundCache;
import org.feuyeux.cache.benchmark.coon.LettuceCache;
import org.feuyeux.cache.benchmark.coon.LmdbCache;
import org.feuyeux.cache.benchmark.coon.OhcCache;
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
import site.ycsb.generator.NumberGenerator;
import site.ycsb.generator.ScrambledZipfianGenerator;

/**
 * @author feuyeux
 */
@State(Scope.Group)
@BenchmarkMode(Throughput)
@Slf4j
public class AllBenchmark {

  @Param({"Lettuce", "Lmdb", "Ohc", "Caffeine", "Cache2k"})
  private String type;

  private static BasicCache<String, String> cache;
  private static String[] values;
  private static final int SIZE = 10_0000;
  private static final int MASK = SIZE - 1;
  private static final int ITEMS = SIZE / 3;

  @State(Scope.Thread)
  public static class ThreadState {

    static final Random RANDOM = new Random();
    int index = RANDOM.nextInt();
  }

  @Setup
  public void setup() {
    values = new String[SIZE];
    switch (type) {
      case "Lettuce" -> cache = new LettuceCache();
      case "Lmdb" -> cache = new LmdbCache<>();
      case "Ohc" -> cache = new OhcCache<>();
      case "Ground" -> cache = new GroundCache<>();
      case "Caffeine" -> cache = new CaffeineCache<>(2 * SIZE);
      case "Cache2k" -> cache = new Cache2kCache<>(2 * SIZE);
      default -> throw new AssertionError("Unknown type: " + type);
    }
    // Populate using realistic access distribution
    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      values[i] = generator.nextValue().toString();
      cache.put(values[i], String.valueOf(i), true);
    }
    log.info("[{}] Tear up done.", type);
  }

  @TearDown(Level.Iteration)
  public void tearDown() {
    cache.stats();
    log.info("[{}] Tear down finished.", type);
  }

  @Benchmark
  @Group("WO")
  @GroupThreads(20)
  public void writeOnly(AllBenchmark.ThreadState threadState) {
    String key = values[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("RO")
  @GroupThreads(20)
  public String readOnly(JvmBenchmark.ThreadState threadState) {
    String key = values[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(16)
  public String get(ThreadState threadState) {
    String key = values[threadState.index++ & MASK];
    String v = cache.get(key);
    log.info("[{}]v:{}", type, v);
    return v;
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(4)
  public void put(ThreadState threadState) {
    String key = values[threadState.index++ & MASK];
    cache.put(key, key);
  }
}
