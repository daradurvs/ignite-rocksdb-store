package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

/**
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
@RunWith(Parameterized.class)
public class SerializerImplTest {
    private static final String LINE = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private Serialiazer serializer;

    public SerializerImplTest(Serialiazer serializer) {
        this.serializer = serializer;
    }

    @Parameterized.Parameters (name = "{0}")
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

    private Object serializeDeserialize(Object obj) {
        byte[] arr = serializer.serialize(obj);

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

            if (i != object.i)
                return false;
            return s.equals(object.s);
        }

        @Override public int hashCode() {
            return 0;
        }
    }
}
