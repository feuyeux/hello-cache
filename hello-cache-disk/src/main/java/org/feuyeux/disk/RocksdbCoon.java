package org.feuyeux.disk;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * @author feuyeux
 */
// https://github.com/facebook/rocksdb
@Slf4j
public class RocksdbCoon<T> {

  private static final String ROCKSDB_PATH = "/tmp/hello-rocksdb";
  // the Options class contains a set of configurable DB options
  // that determines the behaviour of the database.
  Options options;
  private RocksDB db;

  public RocksdbCoon() {
    this(ROCKSDB_PATH);
  }

  public RocksdbCoon(String rocksdbPath) {
    try {
      options = new Options().setCreateIfMissing(true);
      db = RocksDB.open(options, rocksdbPath);
    } catch (RocksDBException e) {
      log.error("", e);
    }
  }

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

  public void stats() {}
}
