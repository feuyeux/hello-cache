SHELL_FOLDER=$(cd "$(dirname "$0")" || exit;pwd)
cd "$SHELL_FOLDER" || exit
outputDir=data
if [[ ! -e $outputDir ]]; then
    mkdir $outputDir
fi
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
test_name="All_benchmark"
cd ..
mvn clean install -DskipTests
mvn -v
sleep 3
sh docker.sh
echo
echo "Start BenchMark: $test_name"
java -jar target/hello-cache-benchmark.jar AllBenchmark \
    -jvmArgs "-server -Xms5G -Xmx5G -XX:+PrintGCDetails --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 1 -w 1s -i 1 -r 5s -t 2 -tu ms \
    - -foe true \
    -p type=Ground,Lettuce \
    -rf json \
    -rff data/$test_name.json \
    -o data/$test_name.out

#    -p type=Ohc,Caffeine,Cache2k,Lettuce,Lmdb \