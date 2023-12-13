package org.feuyeux.hello.cache;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.feuyeux.hello.cache.coon.BasicCache;
import org.feuyeux.hello.cache.coon.Cache2kCache;
import org.feuyeux.hello.cache.coon.CaffeineCache;
import org.feuyeux.hello.cache.coon.ConcurrentMapCache;
import org.feuyeux.hello.cache.coon.GuavaCache;
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
public class JvmBenchmark {

  private static final int SIZE = (2 << 16);
  private static final int MASK = SIZE - 1;
  private static final int ITEMS = SIZE / 3;

  @Param({"CHM", "Cache2k", "Caffeine", "Guava"})
  String type;
  BasicCache<Integer, Integer> cache;
  Integer[] ints;

  @State(Scope.Thread)
  public static class ThreadState {

    static final Random random = new Random();
    int index = random.nextInt();
  }

  @Setup
  public void setup() {
    ints = new Integer[SIZE];
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
      ints[i] = generator.nextValue().intValue();
      cache.put(ints[i], i);
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
    Integer key = ints[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("WO")
  @GroupThreads(8)
  public void writeOnly(ThreadState threadState) {
    Integer key = ints[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(6)
  public Integer readwrite_get(ThreadState threadState) {
    Integer key = ints[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(2)
  public void readwrite_put(ThreadState threadState) {
    Integer key = ints[threadState.index++ & MASK];
    cache.put(key, key);
  }

/*    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(JvmBenchmark.class.getSimpleName())
                .param("computeType", "Cache2k", "Caffeine", "Guava", "CHM")
                .result("compute_benchmark.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }*/
}
