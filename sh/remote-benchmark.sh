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
test_name="Remote_benchmark"
echo "Start BenchMark: $test_name"
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
#"Redisson", "Lettuce", "Jedis"
java -jar hello-cache-app/target/hello-cache-app.jar RemoteBenchmark \
    -jvmArgs "-server -Xms4G -Xmx4G --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 2 -w 1s -i 2 -r 5s -t 2 -tu s \
    - -foe true \
    -p type=Jedis,Lettuce,Redisson \
    -rf json \
    -rff data/$test_name.json \
    -o data/$test_name.out
