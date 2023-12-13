package org.feuyeux.hello.cache;

import static org.openjdk.jmh.annotations.Mode.Throughput;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.coon.BasicCache;
import org.feuyeux.hello.cache.coon.GroundCache;
import org.feuyeux.hello.cache.coon.LettuceObjectCache;
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
public class BytesBenchmark {

  @Param({"Lettuce", "Ground"})
  private String type;
  private static BasicCache<String, byte[]> cache;
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
      case "Lettuce" -> cache = new LettuceObjectCache<>();
      case "Ground" -> cache = new GroundCache<>();
      default -> throw new AssertionError("Unknown type: " + type);
    }
    // Populate using realistic access distribution
    NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
    for (int i = 0; i < SIZE; i++) {
      ints[i] = generator.nextValue().toString();
      cache.put(ints[i],
          String.valueOf(i).getBytes(StandardCharsets.UTF_8), true);
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
  @GroupThreads(10)
  public void writeOnly(BytesBenchmark.ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
    cache.put(key, bytes);
  }

  @Benchmark
  @Group("RO")
  @GroupThreads(10)
  public void readOnly(JvmBenchmark.ThreadState threadState, Blackhole blackhole) {
    String key = ints[threadState.index++ & MASK];
    byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
    blackhole.consume(bytes);
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(8)
  public void get(ThreadState threadState, Blackhole blackhole) {
    String key = ints[threadState.index++ & MASK];
    blackhole.consume(cache.get(key));
  }

  @Benchmark
  @Group("RW")
  @GroupThreads(2)
  public void put(ThreadState threadState) {
    String key = ints[threadState.index++ & MASK];
    byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
    cache.put(key, bytes);
  }
}
