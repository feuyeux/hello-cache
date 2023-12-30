package org.feuyeux.disk.serialize;

import java.nio.ByteBuffer;

public class BytesSerializer {

  public void serialize(byte[] bytes, ByteBuffer buf) {
    // 用前16位记录数组长度
    buf.put((byte) ((bytes.length >>> 8) & 0xFF));
    buf.put((byte) ((bytes.length) & 0xFF));
    buf.put(bytes);
  }

  public byte[] deserialize(ByteBuffer buf) {
    // 判断字节数组的长度
    int length = (((buf.get() & 0xff) << 8) + ((buf.get() & 0xff)));
    byte[] bytes = new byte[length];
    // 读取字节数组
    buf.get(bytes);
    return bytes;
  }

  public int serializedSize(byte[] bytes) {
    // 设置字符串长度限制，2^16 = 65536
    if (bytes.length > 65536) {
      throw new RuntimeException("encoded string too long: " + bytes.length + " bytes");
    }
    // 设置字符串长度限制，2^16 = 65536
    return bytes.length + 2;
  }
}
