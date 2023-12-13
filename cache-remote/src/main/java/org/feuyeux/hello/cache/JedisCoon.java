package org.feuyeux.hello.cache;

import static org.feuyeux.hello.cache.ConnectionConf.host;
import static org.feuyeux.hello.cache.ConnectionConf.port;

import java.time.Duration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


//https://github.com/redisson/redisson-examples
@Slf4j
@Service
public class JedisCoon {

  private JedisPool jedisPool;

  private JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(60));
    poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
    poolConfig.setNumTestsPerEvictionRun(-1);
    poolConfig.setMaxTotal(20);
    poolConfig.setMaxIdle(2);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    return poolConfig;
  }

  @PostConstruct
  public void init() {
    jedisPool = new JedisPool(buildPoolConfig(), host, port, 10_000);
  }

  @PreDestroy
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

  public void stats() {

  }
}
