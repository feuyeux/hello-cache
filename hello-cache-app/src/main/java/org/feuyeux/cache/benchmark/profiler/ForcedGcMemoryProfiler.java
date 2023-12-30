package org.feuyeux.cache.benchmark.profiler;

/*
 * #%L
 * Benchmarks: JMH suite.
 * %%
 * Copyright (C) 2013 - 2021 headissue GmbH, Munich
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.*;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.runner.IterationType;
import org.openjdk.jmh.util.Utils;

/**
 * Record the used heap memory of a benchmark iteration by forcing a full garbage collection.
 * Experimental, not recommended for usage. Use {@link HeapProfiler} instead
 *
 * @author Jens Wilke
 */
@Slf4j
public class ForcedGcMemoryProfiler implements InternalProfiler {

  private static final boolean RUNONLYAFTERLASTITERATION = true;

  @SuppressWarnings("unused")
  private static Object keepReference;

  private static long gcTimeMillis = -1;
  private static long usedHeapViaHistogram = -1;
  private static volatile boolean enabled = false;
  private static UsageTuple usageAfterIteration;
  private static UsageTuple usageAfterSettled;

  /**
   * The benchmark needs to hand over the reference so the memory is kept after the shutdown of the
   * benchmark and can be measured.
   */
  public static void keepReference(Object rootReferenceToKeep) {
    if (enabled) {
      keepReference = rootReferenceToKeep;
    }
  }

  public static UsageTuple getUsage() {
    MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
    long usedHeapMemory = heapUsage.getUsed();
    long usedNonHeap = nonHeapUsage.getUsed();
    log.info(
        "[getMemoryMXBean] usedHeap={}, usedNonHeap={}, totalUsed={}",
        usedHeapMemory,
        usedNonHeap,
        (usedHeapMemory + usedNonHeap));
    log.info(
        "[Runtime totalMemory-freeMemory] used memory: {}",
        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
    return new UsageTuple(heapUsage, nonHeapUsage);
  }

  /**
   * Called from the benchmark when the objects are still referenced to record the used memory.
   * Enforces a full garbage collection and records memory usage. Waits and triggers GC again, as
   * long as the memory is still reducing. Some workloads needs some time until they drain queues
   * and finish all the work.
   */
  public static void recordUsedMemory() {
    long t0 = System.currentTimeMillis();
    long usedMemorySettled;
    if (runSystemGC()) {
      usageAfterIteration = getUsage();
      long m2 = usageAfterIteration.getTotalUsed();
      do {
        try {
          Thread.sleep(567);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        runSystemGC();
        usedMemorySettled = m2;
        usageAfterSettled = getUsage();
        m2 = usageAfterSettled.getTotalUsed();
      } while (m2 < usedMemorySettled);
      gcTimeMillis = System.currentTimeMillis() - t0;
    }
    usedHeapViaHistogram = printHeapHistogram(System.out, 30);
  }

  public static boolean runSystemGC() {
    List<GarbageCollectorMXBean> enabledBeans = new ArrayList<>();

    for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
      long count = bean.getCollectionCount();
      if (count != -1) {
        enabledBeans.add(bean);
      }
    }

    long beforeGcCount = countGc(enabledBeans);

    System.gc();

    final int MAXWAITMSECS = 20 * 1000;
    final int STABLETIMEMSECS = 500;

    if (enabledBeans.isEmpty()) {
      log.info("WARNING: MXBeans can not report GC info.");
      return false;
    }

    boolean gcHappened = false;

    long start = System.nanoTime();
    long gcHappenedTime = 0;
    while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < MAXWAITMSECS) {
      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      long afterGcCount = countGc(enabledBeans);

      if (!gcHappened) {
        if (afterGcCount - beforeGcCount >= 2) {
          gcHappened = true;
        }
      }
      if (gcHappened) {
        if (afterGcCount == beforeGcCount) {
          if (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - gcHappenedTime) > STABLETIMEMSECS) {
            return true;
          }
        } else {
          gcHappenedTime = System.nanoTime();
          beforeGcCount = afterGcCount;
        }
      }
    }
    if (gcHappened) {
      log.info(
          "WARNING: System.gc() was invoked but unable to wait while GC stopped, is GC too asynchronous?");
    } else {
      log.info(
          "WARNING: System.gc() was invoked but couldn't detect a GC occurring, is System.gc() disabled?");
    }
    return false;
  }

  private static long countGc(final List<GarbageCollectorMXBean> enabledBeans) {
    long cnt = 0;
    for (GarbageCollectorMXBean bean : enabledBeans) {
      cnt += bean.getCollectionCount();
    }
    return cnt;
  }

  public static String getJmapExcutable() {
    String javaHome = System.getProperty("java.home");
    String jreDir = File.separator + "jre";
    if (javaHome.endsWith(jreDir)) {
      javaHome = javaHome.substring(0, javaHome.length() - jreDir.length());
    }
    return (javaHome
        + File.separator
        + "bin"
        + File.separator
        + "jmap"
        + (Utils.isWindows() ? ".exe" : ""));
  }

  public static long printHeapHistogram(PrintStream out, int maxLines) {
    long totalBytes = 0;
    boolean partial = false;
    try {
      Process proc =
          Runtime.getRuntime()
              .exec(new String[] {getJmapExcutable(), "-histo", Long.toString(Utils.getPid())});
      InputStream in = proc.getInputStream();
      LineNumberReader r = new LineNumberReader(new InputStreamReader(in));
      String s;
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(buffer);
      while ((s = r.readLine()) != null) {
        if (s.startsWith("Total")) {
          ps.println(s);
          String[] sa = s.split("\\s+");
          totalBytes = Long.parseLong(sa[2]);
        } else if (r.getLineNumber() <= maxLines) {
          ps.println(s);
        } else {
          if (!partial) {
            ps.println("[ ... truncated ... ]");
          }
          partial = true;
        }
      }
      r.close();
      in.close();
      ps.close();
      byte[] histoOuptut = buffer.toByteArray();
      buffer = new ByteArrayOutputStream();
      ps = new PrintStream(buffer);
      ps.println("[Heap Histogram Live Objects] used=" + totalBytes);
      ps.write(histoOuptut);
      ps.println();
      ps.close();
      out.write(buffer.toByteArray());
    } catch (Exception ex) {
      log.info("ForcedGcMemoryProfiler: error attaching / reading histogram");
      log.error("ForcedGcMemoryProfiler: error attaching / reading histogram", ex);
    }
    return totalBytes;
  }

  int iterationNumber = 0;

  @Override
  public Collection<? extends Result> afterIteration(
      final BenchmarkParams benchmarkParams,
      final IterationParams iterationParams,
      final IterationResult result) {
    if (RUNONLYAFTERLASTITERATION) {
      if (iterationParams.getType() != IterationType.MEASUREMENT
          || iterationParams.getCount() != ++iterationNumber) {
        return Collections.emptyList();
      }
    }
    recordUsedMemory();
    List<Result> l =
        new ArrayList<>(
            Arrays.asList(
                new OptionalScalarResult(
                    "gcTimeMillis", (double) gcTimeMillis, "ms", AggregationPolicy.AVG),
                new OptionalScalarResult(
                    "usedHeap", (double) usedHeapViaHistogram, "bytes", AggregationPolicy.AVG)));
    if (usageAfterIteration != null) {
      // old metrics, t.b. removed
      l.addAll(
          Arrays.asList(
              new OptionalScalarResult(
                  "used.settled",
                  (double) usageAfterSettled.getTotalUsed(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "used.after",
                  (double) usageAfterIteration.getTotalUsed(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "total",
                  (double) usageAfterSettled.getTotalCommitted(),
                  "bytes",
                  AggregationPolicy.AVG)));
      l.addAll(
          Arrays.asList(
              new OptionalScalarResult(
                  "totalUsed",
                  (double) usageAfterSettled.getTotalUsed(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "totalUsed.after",
                  (double) usageAfterIteration.getTotalUsed(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "totalCommitted",
                  (double) usageAfterSettled.getTotalCommitted(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "totalCommitted.after",
                  (double) usageAfterIteration.getTotalCommitted(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "heapUsed",
                  (double) usageAfterSettled.heap.getUsed(),
                  "bytes",
                  AggregationPolicy.AVG),
              new OptionalScalarResult(
                  "heapUsed.after",
                  (double) usageAfterIteration.heap.getUsed(),
                  "bytes",
                  AggregationPolicy.AVG)));
    }
    keepReference = null;
    return l;
  }

  @Override
  public void beforeIteration(
      final BenchmarkParams benchmarkParams, final IterationParams iterationParams) {
    usageAfterIteration = usageAfterSettled = null;
    enabled = true;
  }

  @Override
  public String getDescription() {
    return "Adds used memory to the result, if recorded via recordUsedMemory()";
  }

  static class UsageTuple {

    MemoryUsage heap;
    MemoryUsage nonHeap;

    public UsageTuple(final MemoryUsage heapUsage, final MemoryUsage nonHeapUsage) {
      heap = heapUsage;
      nonHeap = nonHeapUsage;
    }

    public long getTotalUsed() {
      return heap.getUsed() + nonHeap.getUsed();
    }

    public long getTotalCommitted() {
      return heap.getCommitted() + nonHeap.getCommitted();
    }
  }
}
