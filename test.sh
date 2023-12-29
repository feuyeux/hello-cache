#!/usr/bin/env bash
set -e
SCRIPT_PATH="$(
  cd "$(dirname "$0")" >/dev/null 2>&1 || exit
  pwd -P
)"
cd "$SCRIPT_PATH" || exit
cd cache-app
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home
java -version
#mvn clean test -Dtest=HelloCacheApplicationTests#redisson -DfailIfNoTests=false
#mvn clean test -Dtest=HelloCacheApplicationTests#lettuce -DfailIfNoTests=false
mvn clean test -Dtest=HelloCacheApplicationTests -DfailIfNoTests=false