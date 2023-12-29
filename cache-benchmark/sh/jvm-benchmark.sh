mkdir data

export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
test_name="Jvm_benchmark_JDK17_ZGC"
sh ../build.sh
echo "Start BenchMark: $test_name"
java -Djmh.blackhole.autoDetect=false \
    -jar target/hello-cache-benchmark.jar JvmBenchmark \
    -jvmArgs "-server -Xms4G -Xmx4G -Xlog:gc* -XX:+UnlockExperimentalVMOptions -XX:+UseZGC --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \" \
    -f 2 -wi 2 -w 2s -i 2 -r 4s -t 2 -tu ms \
    -prof org.feuyeux.hello.cache.profiler.ForcedGcMemoryProfiler \
    -p type=Cache2k,Caffeine \
    -rf json \
    -rff data/$test_name.json \
    -o data/$test_name.out
