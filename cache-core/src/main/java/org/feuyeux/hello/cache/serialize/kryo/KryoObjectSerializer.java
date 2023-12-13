package org.feuyeux.hello.cache.serialize.kryo;

import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public record KryoObjectSerializer<T>(Class<T> clazz) {

  public void serialize(T t, ByteBuffer byteBuffer) {
    Assert.notNull(t, "传入对象不可为null！");
    byte[] bytes = KryoSerializationUtils.serialize(t);
    if (bytes != null) {
      byteBuffer.put(bytes);
    }
  }

  public T deserialize(ByteBuffer byteBuffer) {
    byte[] bytes = new byte[byteBuffer.capacity()];
    byteBuffer.get(bytes);
    return KryoSerializationUtils.deserialize(bytes, clazz);
  }

  public int serializedSize(T t) {
    byte[] bytes = KryoSerializationUtils.serialize(t);
    return bytes == null ? 0 : bytes.length;
  }
}
