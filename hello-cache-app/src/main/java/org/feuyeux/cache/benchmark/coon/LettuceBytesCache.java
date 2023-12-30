package org.feuyeux.cache.benchmark.coon;

import java.util.concurrent.CompletionStage;
import org.feuyeux.redis.lettuce.LettuceBytesCoon;

public class LettuceBytesCache implements AsyncCache<byte[], byte[]> {

  private final LettuceBytesCoon coon = new LettuceBytesCoon();

  @Override
  public byte[] get(byte[] key) {
    return coon.get(key);
  }

  @Override
  public void put(byte[] key, byte[] value) {
    coon.put(key, value);
  }

  @Override
  public void remove(byte[] key) {
    coon.remove(key);
  }

  @Override
  public void clear() {}

  @Override
  public void stats() {
    coon.stats();
  }

  public void close() {
    coon.close();
  }

  @Override
  public CompletionStage<byte[]> getAsync(byte[] key) {
    return coon.getAsync(key);
  }

  @Override
  public CompletionStage putAsync(byte[] key, byte[] value) throws Exception {
    return coon.putAsync(key, value);
  }
}
