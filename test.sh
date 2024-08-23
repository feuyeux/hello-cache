#!/usr/bin/env bash
set -e
SCRIPT_PATH="$(
  cd "$(dirname "$0")" >/dev/null 2>&1 || exit
  pwd -P
)"
cd "$SCRIPT_PATH" || exit
export JAVA_HOME=/usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home

docker ps -a | grep "Exited" | awk '{print $1}' | xargs docker rm

if [[ "$(docker ps -a | awk '{print $2}' | grep redis)" != "" ]]; then
	echo "redis is running"
else
	docker run --name hello-redis -p 6379:6379 -d redis
	echo "redis is starting"
fi

mvn clean test