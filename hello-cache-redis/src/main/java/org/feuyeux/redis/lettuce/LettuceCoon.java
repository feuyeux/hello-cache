package org.feuyeux.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.feuyeux.redis.ConnectionConf;

// http://redisdoc.com/index.html
// https://lettuce.io/core/release/reference/index.html
@Slf4j
public class LettuceCoon {

  private RedisClient redisClient;
  private GenericObjectPool<StatefulRedisConnection> pool;

  public LettuceCoon() {
    RedisURI uri = RedisURI.create(ConnectionConf.host, ConnectionConf.port);
    redisClient = RedisClient.create(uri);
    pool =
        ConnectionPoolSupport.createGenericObjectPool(
            () -> redisClient.connect(), new GenericObjectPoolConfig<>());
  }

  public void close() {
    if (pool != null && !pool.isClosed()) {
      pool.close();
    }
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  public String get(String key) {
    try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
      RedisCommands<String, String> commands = connection.sync();
      log.debug("get {}", key);
      return commands.get(key);
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  public RedisFuture<String> getAsync(String key) {
    try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
      RedisAsyncCommands<String, String> commands = connection.async();
      log.debug("get {}", key);
      return commands.get(key);
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  // exValue seconds
  public void put(String key, String value, int exValue) {
    if (value != null) {
      try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
        SetArgs ex = SetArgs.Builder.ex(exValue);
        RedisCommands<String, String> commands = connection.sync();
        commands.set(key, value, ex);
      } catch (Exception e) {
        log.error("", e);
      }
    }
  }

  public boolean put(String key, String value) {
    log.debug("put {}:{}", key, value);
    if (value != null) {
      try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
        RedisCommands<String, String> commands = connection.sync();
        commands.set(key, value);
        return true;
      } catch (Exception e) {
        log.error("", e);
      }
    }
    return false;
  }

  public RedisFuture<String> putAsync(String key, String value) throws Exception {
    log.debug("putAsync {}:{}", key, value);
    try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
      RedisAsyncCommands<String, String> commands = connection.async();
      return commands.set(key, value);
    }
  }

  public Long remove(String... keys) {
    try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
      RedisCommands<String, String> commands = connection.sync();
      return commands.del(keys);
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  public void stats() {}
}
