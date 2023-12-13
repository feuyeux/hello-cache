package org.feuyeux.hello.cache;

import static java.nio.ByteBuffer.allocateDirect;
import static org.lmdbjava.DbiFlags.MDB_CREATE;
import static org.lmdbjava.GetOp.MDB_SET_KEY;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.feuyeux.hello.cache.serialize.spring.ObjectSerializer;
import org.lmdbjava.Cursor;
import org.lmdbjava.Dbi;
import org.lmdbjava.Env;
import org.lmdbjava.Stat;
import org.lmdbjava.Txn;
import org.springframework.stereotype.Service;

// https://github.com/lmdbjava/lmdbjava.git
@Slf4j
@Service
public class LmdbCoon<T> {

  private static final String DB_NAME = "hello-lmdb";
  private Env<ByteBuffer> env;
  private Dbi<ByteBuffer> db;

  private static final long lmdbMaxSizeGb = 2048;
  private static final int lmdbDatabaseCount = 1;
  private static final int lmdbDatabaseReader = 20;
  private static final String lmdbPath = "/tmp/lmdb/coon";
  private final ObjectSerializer<T> valueSerializer = new ObjectSerializer<>();

  @PostConstruct
  public void init() {
    log.info("Lmdb initializing...");
    log.info("FILEPATH：{}", lmdbPath);
    File file = new File(lmdbPath);
    if (!file.exists()) {
      try {
        boolean mkdir = file.mkdirs();
        log.debug("mkdir?{}", mkdir);
      } catch (Exception e) {
        log.error("", e);
      }
    }
    env = org.lmdbjava.Env.create()
        .setMapSize(lmdbMaxSizeGb * 1024 * 1024 * 1024)
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

  public void put(String key, T value) {
    if (key == null || value == null) {
      return;
    }
    db.put(toBb(key), toBb(value));
  }

  public void putBytes(byte[] key, byte[] value) {
    db.put(toBb(key), toBb(value));
  }

  public T get(String key) {
    try (Txn<ByteBuffer> txn = env.txnRead(); Cursor<ByteBuffer> c = db.openCursor(txn)) {
      c.get(toBb(key), MDB_SET_KEY);
      ByteBuffer byteBuffer = c.val();
      return valueSerializer.deserialize(byteBuffer);
    }
  }

  public byte[] getBytes(byte[] key) {
    try (Txn<ByteBuffer> txn = env.txnRead(); Cursor<ByteBuffer> c = db.openCursor(txn)) {
      c.get(toBb(key), MDB_SET_KEY);
      ByteBuffer byteBuffer = c.val();
      byte[] bytes = new byte[byteBuffer.remaining()];
      byteBuffer.get(bytes);
      return bytes;
    }
  }

  public void removeBytes(byte[] key) {
    db.delete(toBb(key));
  }

  public void remove(String key) {
    db.delete(toBb(key));
  }

  ByteBuffer toBb(final T value) {
    int size = valueSerializer.serializedSize(value);
    final ByteBuffer bb = allocateDirect(size);
    valueSerializer.serialize(value, bb);
    bb.flip();
    return bb;
  }

  ByteBuffer toBb(final String value) {
    byte[] val = value.getBytes(StandardCharsets.UTF_8);
    final ByteBuffer bb = allocateDirect(val.length);
    bb.put(val).flip();
    return bb;
  }

  ByteBuffer toBb(final byte[] value) {
    final ByteBuffer bb = allocateDirect(value.length);
    bb.put(value).flip();
    return bb;
  }

  ByteBuffer bb(final int value) {
    ByteBuffer bb = allocateDirect(java.lang.Integer.BYTES);
    bb.putInt(value).flip();
    return bb;
  }

  public void stats() {
    try (Txn<ByteBuffer> txn = env.txnRead()) {
      Stat stat = db.stat(txn);
      log.info("Lmdb stat:{}", stat);
    }
  }
}
