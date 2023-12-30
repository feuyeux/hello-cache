https://github.com/cache2k/cache2k-benchmark.git

```sh
$ export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
$ mvn -DskipTests clean install
$ bash jmh-jvm-benchmark.sh --diligent --dry complete

## caffeine-ZipfianSequenceLoadingBenchmark-2        
$ java -jar jmh-suite/target/benchmarks.jar \\.ZipfianSequenceLoadingBenchmark \
    -jvmArgs -server\ -XX:BiasedLockingStartupDelay=0 \
    -f 3 -wi 2 -w 10s -i 2 -r 10s \
    -prof comp \
    -prof gc \
    -prof org.cache2k.benchmark.jmh.MiscResultRecorderProfiler \
    -prof org.cache2k.benchmark.jmh.GcProfiler \
    -prof org.cache2k.benchmark.jmh.HeapProfiler \
    -t 2 \
    -p shortName=caffeine \
    -p cacheFactory=org.cache2k.benchmark.cache.CaffeineCacheFactory \
    -rf json \
    -rff /Users/han/jmh-result/result-caffeine-ZipfianSequenceLoadingBenchmark-2.json
$ brew install asciidoctor jq gnuplot
$ bash processJmhResults.sh --dir /Users/han/jmh-result process
```