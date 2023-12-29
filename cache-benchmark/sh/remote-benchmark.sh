outputDir=data
if [[ ! -e $outputDir ]]; then
    mkdir $outputDir
fi
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
test_name="Remote_benchmark"
cd ..
mvn clean install -DskipTests
echo "Start BenchMark: $test_name"
java -jar target/hello-cache-benchmark.jar RemoteBenchmark \
    -jvmArgs "-server -Xms4G -Xmx4G --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 2 -w 1s -i 2 -r 5s -t 2 -tu s \
    - -foe true \
    -p type=Lettuce,Redisson \
    -rf json \
    -rff data/$test_name.json \
    -o data/$test_name.out
