package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import com.sbt.dpl.gridgain.AffinityParticleKey;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests implementations of {@link Serialiazer} interface.
 */
@RunWith(Parameterized.class)
public class SerializerImplTest {
    private static final String LINE = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private Serialiazer serializer;

    public SerializerImplTest(Serialiazer serializer) {
        this.serializer = serializer;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Serialiazer> instancesToTest() {
        return Arrays.<Serialiazer>asList(
            new JavaSerializer(),
            new KryoSerializer()
        );
    }

    @Test
    public void testCustomObjectSerialization() throws Exception {
        TestObject sut = new TestObject();

        assertEquals(sut, serializeDeserialize(sut));
    }

    @Test
    public void testStringSerialization() throws Exception {
        String sut = LINE;

        assertEquals(sut, serializeDeserialize(sut));
    }

    @Test
    public void testNullSerialization() throws Exception {
        assertNull(serializeDeserialize(null));
    }

    @Test
    public void testNullSerialization3() throws Exception {
        AffinityParticleKey sut = new AffinityParticleKey(1, 1, 1);

        assertEquals(sut, serializeDeserialize(new AffinityParticleKey(1, 1, 1)));
    }

    private Object serializeDeserialize(Object obj) {
        byte[] arr = serializer.serialize(obj);

        if (arr == null)
            return null;

        return serializer.deserialize(arr);
    }

    private static class TestObject implements Serializable {
        int i = 1;
        String s = "TestString";

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            TestObject object = (TestObject)o;

            return i == object.i && s.equals(object.s);
        }

        @Override public int hashCode() {
            return 0;
        }
    }
}
