package org.feuyeux.hello.cache;

import java.util.concurrent.TimeUnit;
import org.feuyeux.hello.cache.profiler.ForcedGcMemoryProfiler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 3)
@Threads(4)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class HelloJMH {

  @Param(value = {"10", "50", "100"})
  private int length;

  @Benchmark
  public void testStringBuilder(Blackhole blackhole) {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      str.append(i);
    }
    blackhole.consume(str.toString());
  }

  @Benchmark
  public void testStringAdd(Blackhole blackhole) {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      str.append(i);
    }
    blackhole.consume(str.toString());
  }

  public static void main(String[] args) throws RunnerException {
    // 预期：拼接字符次数越多，StringBuilder.append() 的性能越好
    Options opt = new OptionsBuilder()
        .include(HelloJMH.class.getSimpleName())
        .result("result.json")
        .addProfiler(GCProfiler.class)
        .addProfiler(ForcedGcMemoryProfiler.class)
        .resultFormat(ResultFormatType.JSON).build();
    new Runner(opt).run();
  }
}
