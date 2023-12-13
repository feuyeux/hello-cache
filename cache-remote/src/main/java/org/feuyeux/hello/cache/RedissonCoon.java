package org.feuyeux.hello.cache;

import static org.feuyeux.hello.cache.ConnectionConf.host;
import static org.feuyeux.hello.cache.ConnectionConf.port;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;


//https://github.com/redisson/redisson-examples
@Slf4j
@Service
public class RedissonCoon {

  private RedissonClient redisson;

  @PostConstruct
  public void init() {
    String address = "redis://" + host + ":" + port;
    Config config = new Config();
    config.useSingleServer().setAddress(address);
    log.debug("try to connect redis:{}", address);
    redisson = Redisson.create(config);
  }

  @PreDestroy
  public void close() {
    if (redisson != null && !redisson.isShutdown()) {
      redisson.shutdown();
    }
  }

  public RFuture putAsync(String key, String value) {
    return redisson.getBucket(key).setAsync(value);
  }

  public void put(String key, String value) {
    log.debug("put {}:{}", key, value);
    try {
      redisson.getBucket(key).set(value);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  public RFuture<String> getAsync(String key) {
    try {
      RBucket<String> bucket = redisson.getBucket(key);
      return bucket.getAsync();
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  public String get(String key) {
    log.debug("get {}", key);
    try {
      RBucket<String> bucket = redisson.getBucket(key);
      return bucket.get();
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  public void remove(String key) {
    log.debug("remove {}", key);
    try {
      RBucket<String> bucket = redisson.getBucket(key);
      bucket.delete();
    } catch (Exception e) {
      log.error("", e);
    }
  }
}

