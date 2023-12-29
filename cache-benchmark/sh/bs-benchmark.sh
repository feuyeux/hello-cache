SHELL_FOLDER=$(cd "$(dirname "$0")" || exit;pwd)
outputDir=data
if [[ ! -e $outputDir ]]; then
    mkdir $outputDir
fi
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
test_name="Bytes_benchmark"
cd ..
mvn clean install -DskipTests
mvn -v
sleep 3
sh docker.sh
echo
echo "Start BenchMark: $test_name"
java -jar target/hello-cache-benchmark.jar BytesBenchmark \
    -jvmArgs "-server -Xms6G -Xmx6G -XX:+PrintGCDetails -XX:MaxDirectMemorySize=1G --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 1 -w 1s -i 3 -r 5s -t 2 -tu ms \
    - -foe true \
    -p type=Ground,Lettuce \
    -rf json \
    -rff data/$test_name.json \
    -o data/$test_name.out

#    -p type=Ohc,Caffeine,Cache2k,Lettuce,Lmdb \