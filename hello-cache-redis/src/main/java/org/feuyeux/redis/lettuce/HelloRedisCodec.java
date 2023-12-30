package org.feuyeux.redis.lettuce;

import io.lettuce.core.codec.RedisCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloRedisCodec<V> implements RedisCodec<String, V> {

  private final Charset charset = Charset.forName("UTF-8");

  @Override
  public String decodeKey(ByteBuffer bytes) {
    return charset.decode(bytes).toString();
  }

  @Override
  public V decodeValue(ByteBuffer bytes) {
    try {
      byte[] array = new byte[bytes.remaining()];
      bytes.get(array);
      ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array));
      return (V) is.readObject();
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public ByteBuffer encodeKey(String key) {
    return charset.encode(key);
  }

  @Override
  public ByteBuffer encodeValue(V value) {
    try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
      ObjectOutputStream os = new ObjectOutputStream(bytes);
      os.writeObject(value);
      return ByteBuffer.wrap(bytes.toByteArray());
    } catch (IOException e) {
      log.error("", e);
      return null;
    }
  }
}
