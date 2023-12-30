package org.feuyeux.memory;

import java.nio.ByteBuffer;
import org.caffinitas.ohc.CacheSerializer;
import org.feuyeux.memory.serialize.spring.ObjectSerializer;

public class OhcObjectSerializer<T> implements CacheSerializer<T> {

  private final ObjectSerializer<T> serializer = new ObjectSerializer<>();

  // ProtostuffObjectSerializer protostuffObjectSerializer=new ProtostuffObjectSerializer( );
  // KryoObjectSerializer kryoObjectSerializer=new KryoObjectSerializer( );

  @Override
  public void serialize(T value, ByteBuffer buf) {
    serializer.serialize(value, buf);
  }

  @Override
  public T deserialize(ByteBuffer buf) {
    return serializer.deserialize(buf);
  }

  @Override
  public int serializedSize(T value) {
    return serializer.serializedSize(value);
  }
}
