SHELL_FOLDER=$(
    cd "$(dirname "$0")" || exit
    pwd
)
cd "$SHELL_FOLDER" || exit
pwd
outputDir=data
if [[ ! -e $outputDir ]]; then
    mkdir $outputDir
fi

export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
test_name="Local_benchmark"
cd ..
mvn clean install -DskipTests
PWD
echo "Start BenchMark: $test_name"
java \
    -jar target/hello-cache-benchmark.jar LocalBenchmark \
    -jvmArgs "-server -Xms4G -Xmx4G -XX:+PrintGCDetails --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" \
    -f 1 -wi 1 -w 1s -i 3 -r 4s -t 2 -tu ms \
    - -foe true \
    -prof gc \
    -p type=Lmdb,Rocks \
    -rf json \
    -rff data/$test_name.json \
    -o data/$test_name.out
