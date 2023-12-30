#!/usr/bin/env bash
set -e
SCRIPT_PATH="$(
  cd "$(dirname "$0")" >/dev/null 2>&1 || exit
  pwd -P
)"
cd "$SCRIPT_PATH" || exit
test_name="Jvm_benchmark_ZGC"
sh ../build.sh
cd "$SCRIPT_PATH" && cd ..
oDir=data
if [[ ! -e ${oDir} ]]; then
    mkdir ${oDir}
fi

# "CHM", "Cache2k", "Caffeine", "Guava"
echo "Start BenchMark: $test_name"
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
java -Djmh.blackhole.autoDetect=false \
    -jar hello-cache-app/target/hello-cache-app.jar JvmBenchmark \
    -jvmArgs "-server -Xms4G -Xmx4G -Xlog:gc* -XX:+UnlockExperimentalVMOptions -XX:+UseZGC --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \" \
    -f 2 -wi 2 -w 2s -i 2 -r 4s -t 2 -tu ms \
    -prof org.feuyeux.cache.benchmark.profiler.ForcedGcMemoryProfiler \
    -p type=Cache2k,Caffeine,Guava,CHM \
    -rf json \
    -rff ${oDir}/${test_name}.json \
    -o ${oDir}/${test_name}.out
