package org.feuyeux.hello.cache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Service;

// https://github.com/facebook/rocksdb
@Slf4j
@Service
public class RocksdbCoon {

  private static final String rocksdbPath = "/tmp/hello-rocksdb";
  // the Options class contains a set of configurable DB options
  // that determines the behaviour of the database.
  Options options;
  private RocksDB db;

  @PostConstruct
  public void init() {
    try {
      options = new Options().setCreateIfMissing(true);
      db = RocksDB.open(options, rocksdbPath);
    } catch (RocksDBException e) {
      log.error("", e);
    }
  }

  @PreDestroy
  public void destroy() {
    if (db != null) {
      db.close();
    }
    if (options != null) {
      options.close();
    }
  }

  public void put(String key, String value) {
    if (key == null || value == null) {
      return;
    }
    try {
      db.put(key.getBytes(), value.getBytes());
    } catch (RocksDBException e) {
      log.error("", e);
    }
  }

  public String get(String key) {
    try {
      byte[] bytes = db.get(key.getBytes());
      return new String(bytes);
    } catch (RocksDBException e) {
      log.error("", e);
      return null;
    }
  }

  public void remove(String key) {
    try {
      db.delete(key.getBytes());
    } catch (RocksDBException e) {
      log.error("", e);
    }
  }

  public void stats() {

  }
}

