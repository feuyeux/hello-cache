package org.feuyeux.hello.cache.lettuce;

import static org.feuyeux.hello.cache.ConnectionConf.host;
import static org.feuyeux.hello.cache.ConnectionConf.port;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LettuceObjectCoon<V> {

  private RedisClient redisClient;
  private GenericObjectPool<StatefulRedisConnection<String, V>> pool;
  private StatefulRedisConnection<String, V> connection;

  @PostConstruct
  public void init() {
    RedisURI uri = RedisURI.create(host, port);
    redisClient = RedisClient.create(uri);
    pool = ConnectionPoolSupport.createGenericObjectPool(
        () -> redisClient.connect(new HelloRedisCodec<V>()),
        new GenericObjectPoolConfig<>()
    );
    try {
      connection = pool.borrowObject();
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @PreDestroy
  public void close() {
    if (pool != null && !pool.isClosed()) {
      pool.close();
    }
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  public V get(String key) {
    RedisCommands<String, V> commands = connection.sync();
    log.debug("get {}", key);
    return commands.get(key);
  }

  public RedisFuture<V> getAsync(String key) {
    RedisAsyncCommands<String, V> commands = connection.async();
    log.debug("get {}", key);
    return commands.get(key);
  }

  //exValue seconds
  public void put(String key, V value, int exValue) {
    if (value != null) {
      SetArgs ex = SetArgs.Builder.ex(exValue);
      RedisCommands<String, V> commands = connection.sync();
      commands.set(key, value, ex);
    }
  }

  public void put(String key, V value) {
    if (value != null) {
      RedisCommands<String, V> commands = connection.sync();
      commands.set(key, value);
    }
  }

  public RedisFuture putAsync(String key, V value) throws Exception {
    RedisAsyncCommands<String, V> commands = connection.async();
    return commands.set(key, value);
  }

  public void remove(String key) {
    RedisCommands<String, V> commands = connection.sync();
    commands.del(key);
  }

  public void stats() {

  }
}
