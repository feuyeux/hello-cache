docker ps -a | grep "Exited" | awk '{print $1}' | xargs docker rm

if [[ "$(docker ps -a | awk '{print $2}' | grep redis)" != "" ]]; then
	echo "redis is running"
else
	docker run --name hello-redis -p 6379:6379 -d redis
	echo "redis is starting"
fi
