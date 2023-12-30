package org.feuyeux.memory;

import java.nio.ByteBuffer;
import org.caffinitas.ohc.CacheSerializer;
import org.feuyeux.memory.serialize.StringSerializer;

public class OhcStringSerializer implements CacheSerializer<String> {

  private final StringSerializer stringSerializer = new StringSerializer();

  @Override
  public void serialize(String value, ByteBuffer buf) {
    stringSerializer.serialize(value, buf);
  }

  @Override
  public String deserialize(ByteBuffer buf) {
    return stringSerializer.deserialize(buf);
  }

  @Override
  public int serializedSize(String value) {
    return stringSerializer.serializedSize(value);
  }
}
