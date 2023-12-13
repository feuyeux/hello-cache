package org.feuyeux.hello.cache;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.coon.BasicCache;
import org.feuyeux.hello.cache.coon.Cache2kCache;
import org.feuyeux.hello.cache.coon.CaffeineCache;
import org.feuyeux.hello.cache.coon.GroundCache;
import org.feuyeux.hello.cache.coon.LettuceCache;
import org.feuyeux.hello.cache.coon.LmdbCache;
import org.feuyeux.hello.cache.coon.OhcCache;
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

@State(Scope.Group)
@BenchmarkMode(Throughput)
@Slf4j
public class AllBenchmark {

  @Param({"Lettuce", "Lmdb", "Ohc", "Caffeine", "Cache2k"})
  private String type;
  private static BasicCache<String, String> cache;
  private static String[] ints;
  private static final int SIZE = 10_0000;
  private static final int MASK = SIZE - 1;
  private static final int ITEMS = SIZE / 3;

  @State(Scope.Thread)
  public static class ThreadState {

    static final Random random = new Random();
    int index = random.nextInt();
  }

  @Setup
  public void setup() {
    ints = new String[SIZE];
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
      ints[i] = generator.nextValue().toString();
      cache.put(ints[i], String.valueOf(i), true);
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
    String key = ints[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("RO")
  @GroupThreads(20)
  public String readOnly(JvmBenchmark.ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(16)
  public String get(ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    String v = cache.get(key);
    log.info("[{}]v:{}", type, v);
    return v;
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(4)
  public void put(ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    cache.put(key, key);
  }
}
