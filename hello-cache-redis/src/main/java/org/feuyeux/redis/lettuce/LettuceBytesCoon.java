package org.feuyeux.redis.lettuce;

import static org.feuyeux.redis.ConnectionConf.host;
import static org.feuyeux.redis.ConnectionConf.port;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

// http://redisdoc.com/index.html
// https://lettuce.io/core/release/reference/index.html
@Slf4j
public class LettuceBytesCoon {

  private RedisClient redisClient;
  private GenericObjectPool<StatefulRedisConnection<byte[], byte[]>> pool;

  public LettuceBytesCoon() {
    RedisURI uri = RedisURI.create(host, port);
    redisClient = RedisClient.create(uri);
    pool =
        ConnectionPoolSupport.createGenericObjectPool(
            () -> redisClient.connect(ByteArrayCodec.INSTANCE), new GenericObjectPoolConfig<>());
  }

  public void close() {
    if (pool != null && !pool.isClosed()) {
      pool.close();
    }
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  public byte[] get(byte[] key) {
    try (StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject()) {
      RedisCommands<byte[], byte[]> commands = connection.sync();
      return commands.get(key);
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  public RedisFuture<byte[]> getAsync(byte[] key) {
    try (StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject()) {
      RedisAsyncCommands<byte[], byte[]> commands = connection.async();
      log.debug("get {}", key);
      return commands.get(key);
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  // exValue seconds
  public void put(byte[] key, byte[] value, int exValue) {
    if (value != null) {
      try (StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject()) {
        SetArgs ex = SetArgs.Builder.ex(exValue);
        RedisCommands<byte[], byte[]> commands = connection.sync();
        commands.set(key, value, ex);
      } catch (Exception e) {
        log.error("", e);
      }
    }
  }

  public boolean put(byte[] key, byte[] value) {
    log.debug("put {}:{}", key, value);
    if (value != null) {
      try (StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject()) {
        RedisCommands<byte[], byte[]> commands = connection.sync();
        commands.set(key, value);
        return true;
      } catch (Exception e) {
        log.error("", e);
      }
    }
    return false;
  }

  public RedisFuture putAsync(byte[] key, byte[] value) throws Exception {
    log.debug("putAsync {}:{}", key, value);
    try (StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject()) {
      RedisAsyncCommands<byte[], byte[]> commands = connection.async();
      return commands.set(key, value);
    }
  }

  public void remove(byte[] key) {
    try (StatefulRedisConnection<byte[], byte[]> connection = pool.borrowObject()) {
      RedisCommands<byte[], byte[]> commands = connection.sync();
      commands.del(key);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  public void stats() {}
}
