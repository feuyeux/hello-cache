package org.feuyeux.memory.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.BitSetSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.RegexSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.URISerializer;
import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.guava.ArrayListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ArrayTableSerializer;
import de.javakaffee.kryoserializers.guava.HashBasedTableSerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableTableSerializer;
import de.javakaffee.kryoserializers.guava.LinkedHashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.LinkedListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ReverseListSerializer;
import de.javakaffee.kryoserializers.guava.TreeBasedTableSerializer;
import de.javakaffee.kryoserializers.guava.TreeMultimapSerializer;
import de.javakaffee.kryoserializers.guava.UnmodifiableNavigableSetSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoSerializationUtils<T> {

  /** ThreadLocal -> initialValue */
  private static final KryoPool kryoPool;

  static {
    kryoPool =
        new KryoPool.Builder(
                () -> {
                  Kryo kryo = new Kryo();
                  kryo.setRegistrationRequired(false);
                  kryo.setReferences(true);
                  kryo.setInstantiatorStrategy(
                      new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                  kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());
                  kryo.register(List.of("").getClass(), new ArraysAsListSerializer());
                  kryo.register(
                      Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
                  kryo.register(
                      Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
                  kryo.register(
                      Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
                  kryo.register(
                      Collections.singletonList("").getClass(),
                      new CollectionsSingletonListSerializer());
                  kryo.register(
                      Collections.singleton("").getClass(),
                      new CollectionsSingletonSetSerializer());
                  kryo.register(
                      Collections.singletonMap("", "").getClass(),
                      new CollectionsSingletonMapSerializer());
                  kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
                  kryo.register(InvocationHandler.class, new JdkProxySerializer());
                  kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
                  kryo.register(InvocationHandler.class, new JdkProxySerializer());
                  kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
                  kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
                  kryo.register(Pattern.class, new RegexSerializer());
                  kryo.register(BitSet.class, new BitSetSerializer());
                  kryo.register(URI.class, new URISerializer());
                  kryo.register(UUID.class, new UUIDSerializer());
                  UnmodifiableCollectionsSerializer.registerSerializers(kryo);
                  SynchronizedCollectionsSerializer.registerSerializers(kryo);

                  // now just added some very common classes
                  kryo.register(HashMap.class);
                  kryo.register(ArrayList.class);
                  kryo.register(LinkedList.class);
                  kryo.register(HashSet.class);
                  kryo.register(TreeSet.class);
                  kryo.register(Hashtable.class);
                  kryo.register(Date.class);
                  kryo.register(Calendar.class);
                  kryo.register(ConcurrentHashMap.class);
                  kryo.register(SimpleDateFormat.class);
                  kryo.register(GregorianCalendar.class);
                  kryo.register(Vector.class);
                  kryo.register(BitSet.class);
                  kryo.register(StringBuffer.class);
                  kryo.register(StringBuilder.class);
                  kryo.register(Object.class);
                  kryo.register(Object[].class);
                  kryo.register(String[].class);
                  kryo.register(byte[].class);
                  kryo.register(char[].class);
                  kryo.register(int[].class);
                  kryo.register(float[].class);
                  kryo.register(double[].class);

                  // guava ImmutableList, ImmutableSet, ImmutableMap, ImmutableMultimap,
                  // ImmutableTable, ReverseList, UnmodifiableNavigableSet
                  ImmutableListSerializer.registerSerializers(kryo);
                  ImmutableSetSerializer.registerSerializers(kryo);
                  ImmutableMapSerializer.registerSerializers(kryo);
                  ImmutableMultimapSerializer.registerSerializers(kryo);
                  ImmutableTableSerializer.registerSerializers(kryo);
                  ReverseListSerializer.registerSerializers(kryo);
                  UnmodifiableNavigableSetSerializer.registerSerializers(kryo);
                  // guava ArrayListMultimap, HashMultimap, LinkedHashMultimap, LinkedListMultimap,
                  // TreeMultimap, ArrayTable, HashBasedTable, TreeBasedTable
                  ArrayListMultimapSerializer.registerSerializers(kryo);
                  HashMultimapSerializer.registerSerializers(kryo);
                  LinkedHashMultimapSerializer.registerSerializers(kryo);
                  LinkedListMultimapSerializer.registerSerializers(kryo);
                  TreeMultimapSerializer.registerSerializers(kryo);
                  ArrayTableSerializer.registerSerializers(kryo);
                  HashBasedTableSerializer.registerSerializers(kryo);
                  TreeBasedTableSerializer.registerSerializers(kryo);
                  return kryo;
                })
            .softReferences()
            .build();
  }

  public static byte[] serialize(Object object) {
    if (object == null) {
      return null;
    }
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
    try (Output output = new Output(byteArrayOutputStream)) {
      Kryo kryo = kryoPool.borrow();
      kryo.writeObject(output, object);
      output.flush();
      kryoPool.release(kryo);
    } catch (KryoException ke) {
      throw new IllegalStateException("Failed to serialize object", ke);
    }
    return byteArrayOutputStream.toByteArray();
  }

  public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
    if (bytes == null) {
      return null;
    }
    try (Input input = new Input(new ByteArrayInputStream(bytes))) {
      Kryo kryo = kryoPool.borrow();
      T o = kryo.readObject(input, clazz);
      kryoPool.release(kryo);
      return o;
    } catch (KryoException ke) {
      throw new IllegalStateException("Failed to deserialize object", ke);
    }
  }
}
