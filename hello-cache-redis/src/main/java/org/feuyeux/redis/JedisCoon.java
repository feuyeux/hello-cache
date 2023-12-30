package org.feuyeux.redis;

import static org.feuyeux.redis.ConnectionConf.host;
import static org.feuyeux.redis.ConnectionConf.port;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

// https://github.com/redisson/redisson-examples
@Slf4j
public class JedisCoon {
  private JedisPool jedisPool;

  private JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleDuration(Duration.ofSeconds(60));
    poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
    poolConfig.setNumTestsPerEvictionRun(-1);
    poolConfig.setMaxTotal(20);
    poolConfig.setMaxIdle(2);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    return poolConfig;
  }

  public JedisCoon() {
    jedisPool = new JedisPool(buildPoolConfig(), host, port, 10_000);
  }

  public void close() {
    if (jedisPool != null && !jedisPool.isClosed()) {
      jedisPool.close();
    }
  }

  public void put(String key, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      log.debug("put {}:{}", key, value);
      jedis.set(key, value);
    }
  }

  public String get(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      log.debug("get {}", key);
      return jedis.get(key);
    }
  }

  public void remove(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.del(key);
    }
  }

  public void stats() {}
}
