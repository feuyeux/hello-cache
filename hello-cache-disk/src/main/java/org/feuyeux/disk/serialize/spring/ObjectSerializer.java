package org.feuyeux.disk.serialize.spring;

import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

@Slf4j
public class ObjectSerializer<T> {

  public void serialize(T t, ByteBuffer byteBuffer) {
    byte[] bytes = SerializationUtils.serialize(t);
    if (bytes != null) {
      byteBuffer.put(bytes);
    }
  }

  public T deserialize(ByteBuffer byteBuffer) {
    if (byteBuffer != null) {
      int capacity = byteBuffer.capacity();
      if (capacity > 0) {
        byte[] bytes = new byte[capacity];
        byteBuffer.get(bytes);
        return (T) SerializationUtils.deserialize(bytes);
      }
    }
    return null;
  }

  public int serializedSize(T t) {
    byte[] bytes = SerializationUtils.serialize(t);
    return bytes == null ? 0 : bytes.length;
  }
}
