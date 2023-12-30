#!/usr/bin/env bash
set -e
SCRIPT_PATH="$(
  cd "$(dirname "$0")" >/dev/null 2>&1 || exit
  pwd -P
)"
cd "$SCRIPT_PATH" || exit
#sh ../build.sh
cd "$SCRIPT_PATH" && cd ..
oDir=data
if [[ ! -e ${oDir} ]]; then
    mkdir ${oDir}
fi
test_name="All_benchmark"
sh redis_docker.sh
echo "Start BenchMark: $test_name"
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
java -jar hello-cache-app/target/hello-cache-app.jar AllBenchmark \
    -jvmArgs "-server -Xms5G -Xmx5G -XX:+PrintGCDetails --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 1 -w 1s -i 1 -r 5s -t 2 -tu ms \
    - -foe true \
    -p type=Ground,Lettuce \
    -rf json \
    -rff ${oDir}/${test_name}.json \
    -o ${oDir}/${test_name}.out

#    -p type=Ohc,Caffeine,Cache2k,Lettuce,Lmdb \