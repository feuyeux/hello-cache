package org.feuyeux.cache.benchmark;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.cache2k.Cache2kBuilder;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import site.ycsb.generator.NumberGenerator;
import site.ycsb.generator.ScrambledZipfianGenerator;

@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 5, time = 5)
@Threads(4)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ComputeBenchmarkTest {

  static final int SIZE = (2 << 14);
  static final int MASK = SIZE - 1;
  static final int ITEMS = SIZE / 3;
  static final Integer COMPUTE_KEY = SIZE / 2;
  static final Function<Integer, Boolean> mappingFunction = any -> Boolean.TRUE;
  static final CacheLoader<Integer, Boolean> cacheLoader = CacheLoader.from(key -> Boolean.TRUE);

  @Param({"CHM", "Cache2k", "Caffeine", "Guava"})
  String computeType;

  Function<Integer, Boolean> benchmarkFunction;
  Integer[] values;

  @State(Scope.Thread)
  public static class ThreadState {

    static final Random random = new Random();
    int index = random.nextInt();
  }

  public ComputeBenchmarkTest() {
    values = new Integer[SIZE];
    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      values[i] = generator.nextValue().intValue();
    }
  }

  @Setup
  public void setup() {
    switch (computeType) {
      case "CHM" -> setupConcurrentHashMap();
      case "Caffeine" -> setupCaffeine();
      case "Guava" -> setupGuava();
      case "Cache2k" -> setupCache2k();
      default -> throw new AssertionError("Unknown computingType: " + computeType);
    }
    Arrays.stream(values).forEach(benchmarkFunction::apply);
  }

  @Benchmark
  @Threads(32)
  public Boolean compute_sameKey(ThreadState threadState) {
    return benchmarkFunction.apply(COMPUTE_KEY);
  }

  @Benchmark
  @Threads(32)
  public Boolean compute_spread(ThreadState threadState) {
    return benchmarkFunction.apply(values[threadState.index++ & MASK]);
  }

  private void setupConcurrentHashMap() {
    ConcurrentMap<Integer, Boolean> map = new ConcurrentHashMap<>();
    benchmarkFunction = key -> map.computeIfAbsent(key, mappingFunction);
  }

  private void setupCaffeine() {
    Cache<Integer, Boolean> cache = Caffeine.newBuilder().build();
    benchmarkFunction = key -> cache.get(key, mappingFunction);
  }

  private void setupGuava() {
    com.google.common.cache.LoadingCache<Integer, Boolean> cache =
        CacheBuilder.newBuilder().concurrencyLevel(64).build(cacheLoader);
    benchmarkFunction = cache::getUnchecked;
  }

  private void setupCache2k() {
    org.cache2k.Cache<Integer, Boolean> cache =
        Cache2kBuilder.of(Integer.class, Boolean.class).build();
    benchmarkFunction = cache::get;
  }

  @Test
  public void test() throws RunnerException {
    Options opt =
        new OptionsBuilder()
            .include(ComputeBenchmarkTest.class.getSimpleName())
            .param("computeType", "Cache2k", "Caffeine", "Guava", "CHM")
            .result("compute_benchmark.json")
            .resultFormat(ResultFormatType.JSON)
            .build();
    new Runner(opt).run();
  }
}
