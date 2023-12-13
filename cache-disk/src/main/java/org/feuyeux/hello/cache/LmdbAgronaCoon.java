package org.feuyeux.hello.cache;

import static java.nio.ByteBuffer.allocateDirect;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.DirectBufferProxy.PROXY_DB;
import static org.lmdbjava.GetOp.MDB_SET_KEY;

import java.io.File;
import java.nio.ByteBuffer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.lmdbjava.Cursor;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;
import org.lmdbjava.Stat;
import org.lmdbjava.Txn;
import org.springframework.stereotype.Service;

// https://github.com/lmdbjava/lmdbjava.git
@Slf4j
@Service
public class LmdbAgronaCoon {

  private static final String DB_NAME = "hello-agrona";
  private Env<org.agrona.DirectBuffer> env;
  private Dbi<org.agrona.DirectBuffer> db;

  private static final long lmdbMaxSizeMb = 256;
  private static final int lmdbDatabaseCount = 10;
  private static final int lmdbDatabaseReader = 20;

  @PostConstruct
  public void init() {
    log.info("Lmdb Agrona initializing...");
    String lmdbPath = "/tmp/agrona/coon";
    log.info("FILEPATH：" + lmdbPath);
    File file = new File(lmdbPath);
    if (!file.exists()) {
      try {
        boolean mkdir = file.mkdirs();
        log.debug("mkdir?{}", mkdir);
      } catch (Exception e) {
        log.error("", e);
      }
    }
    env = Env.create(PROXY_DB)
        .setMapSize(lmdbMaxSizeMb * 1024 * 1024)
        .setMaxDbs(lmdbDatabaseCount)
        .setMaxReaders(lmdbDatabaseReader)
        .open(file);
    db = env.openDbi(DB_NAME, MDB_CREATE);
    log.info("Lmdb initializing finished.");
  }

  @PreDestroy
  public void destroy() {
    if (db != null) {
      db.close();
    }
    if (env != null && !env.isClosed()) {
      env.close();
    }
  }

  public void put(String key, String value) {
    try (Txn<org.agrona.DirectBuffer> txn = env.txnWrite()) {
      try (Cursor<org.agrona.DirectBuffer> c = db.openCursor(txn)) {
        c.put(toDb(key), toDb(value));
      }
      txn.commit();
    }
  }

  public String get(String key) {
    try (Txn<org.agrona.DirectBuffer> txn = env.txnRead(); Cursor<org.agrona.DirectBuffer> c = db.openCursor(
        txn)) {
      c.get(toDb(key), MDB_SET_KEY);
      DirectBuffer directBuffer = c.val();
      return directBuffer.getStringUtf8(0);
    }
  }

  public void remove(String key) {
    db.delete(toDb(key));
  }

  MutableDirectBuffer toDb(final String value) {
    final ByteBuffer keyBb = allocateDirect(env.getMaxKeySize());
    final MutableDirectBuffer db = new UnsafeBuffer(keyBb);
    db.putStringUtf8(0, value);
    return db;
  }

  public void stats() {
    try (Txn<DirectBuffer> txn = env.txnRead()) {
      Stat stat = db.stat(txn);
      log.info("Lmdb stat:{}", stat);
    }
  }
}
