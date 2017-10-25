package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

/**
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
public class JavaSerializerTest {
    @org.junit.Test
    public void serialize() throws Exception {
        TestObject sut = new TestObject();

        JavaSerializer serializer = new JavaSerializer();

        byte[] arr = serializer.serialize(sut);

        assertEquals(sut, serializer.deserialize(arr));
    }

    @org.junit.Test
    public void deserialize() throws Exception {
        String sut = "TestString";

        byte[] arr = new JavaSerializer().serialize(sut);

        assertEquals(sut, new JavaSerializer().deserialize(arr));
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