#!/usr/bin/env bash
set -e
SCRIPT_PATH="$(
  cd "$(dirname "$0")" >/dev/null 2>&1 || exit
  pwd -P
)"
cd "$SCRIPT_PATH" || exit
test_name="Local_benchmark"
sh ../build.sh
cd "$SCRIPT_PATH" && cd ..
oDir=data
if [[ ! -e ${oDir} ]]; then
    mkdir ${oDir}
fi
echo "Start BenchMark: $test_name"
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
java \
    -jar hello-cache-app/target/hello-cache-app.jar LocalBenchmark \
    -jvmArgs "-server -Xms4G -Xmx4G -XX:+PrintGCDetails --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 1 -w 1s -i 3 -r 4s -t 2 -tu ms \
    - -foe true \
    -prof gc \
    -p type=Lmdb,Rocks \
    -rf json \
    -rff ${oDir}/${test_name}.json \
    -o ${oDir}/${test_name}.out
