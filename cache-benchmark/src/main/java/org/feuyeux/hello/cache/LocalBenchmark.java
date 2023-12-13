package org.feuyeux.hello.cache;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.coon.BasicCache;
import org.feuyeux.hello.cache.coon.ChronicCache;
import org.feuyeux.hello.cache.coon.LmdbAgronaCache;
import org.feuyeux.hello.cache.coon.LmdbCache;
import org.feuyeux.hello.cache.coon.OhcCache;
import org.feuyeux.hello.cache.coon.RocksdbCache;
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
public class LocalBenchmark {

  @Param({"Jedis", "Lettuce", "Lmdb", "Ohc", "Chronic", "Rocks"})
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
      case "Chronic" -> cache = new ChronicCache();
      case "Lmdb" -> cache = new LmdbCache<>();
      case "Agrona" -> cache = new LmdbAgronaCache();
      case "Ohc" -> cache = new OhcCache<>();
      case "Rocks" -> cache = new RocksdbCache();
      default -> throw new AssertionError("Unknown type: " + type);
    }
    // Populate using realistic access distribution
    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      ints[i] = generator.nextValue().toString();
      cache.put(ints[i], String.valueOf(i));
    }
    log.info("[{}] Tear up done.", type);
  }

  @TearDown(Level.Iteration)
  public void tearDown() {
    //cache.close();
    log.info("[{}] Tear down finished.", type);
  }

  @Benchmark
  @Group("WO")
  @GroupThreads(8)
  public void writeOnly(LocalBenchmark.ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    cache.put(key, key);
  }

  @Benchmark
  @Group("RO")
  @GroupThreads(8)
  public String readOnly(JvmBenchmark.ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(6)
  public String get(ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    return cache.get(key);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(2)
  public void put(ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    cache.put(key, key);
  }

    /*
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(LocalBenchmark.class.getSimpleName())
                .param("type", "Jedis")
                .forks(1)
                .warmupIterations(1).warmupTime(TimeValue.seconds(1))
                .measurementIterations(1).measurementTime(TimeValue.seconds(3))
                .result("benchmark.json")
                .resultFormat(ResultFormatType.JSON)
                .shouldFailOnError(true)
                .build();
        new Runner(opt).run();
    }*/
}
