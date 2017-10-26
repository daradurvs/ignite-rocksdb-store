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

    @Parameterized.Parameters
    public static Collection<Serialiazer> instancesToTest() {
        return Arrays.<Serialiazer>asList(
            new JavaSerializer()
        );
    }

    @Test
    public void serialize() throws Exception {
        TestObject sut = new TestObject();

        byte[] arr = serializer.serialize(sut);

        assertEquals(sut, serializer.deserialize(arr));
    }

    @Test
    public void deserialize() throws Exception {
        String sut = LINE;

        byte[] arr = serializer.serialize(sut);

        assertEquals(sut, serializer.deserialize(arr));
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