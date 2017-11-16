package ru.daradurvs.ignite.cache.store.rocksdb.common;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.RocksDB;

import static org.junit.Assert.assertEquals;

public class ExternalizableAbstractTest {
    static {
        RocksDB.loadLibrary();
    }

    @SuppressWarnings("unchecked")
    protected <T> T serializeDeserialize(Externalizable obj) throws Exception {
        byte[] bytes = serialize(obj);

        return (T)deserialize(obj.getClass(), bytes);
    }

    protected byte[] serialize(Externalizable obj) throws Exception {
        try (ByteOutputStream byteStream = new ByteOutputStream();
             ObjectOutputStream objStream = new ObjectOutputStream(byteStream)) {

            obj.writeExternal(objStream);

            byteStream.flush();

            return byteStream.getBytes();
        }
    }

    protected Object deserialize(Class<? extends Externalizable> cls, byte[] bytes) throws Exception {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objStream = new ObjectInputStream(byteStream)) {

            Externalizable obj = cls.newInstance();

            obj.readExternal(objStream);

            return obj;
        }
    }

    protected void checkEqualityByGetters(@NotNull Object excepted, @NotNull Object actual) throws Exception {
        assertEquals(excepted.getClass(), actual.getClass());

        for (Method method : excepted.getClass().getMethods()) {
            method.setAccessible(true);

            if (method.getName().startsWith("set")) {
                String setterName = method.getName();
                String getterName = setterName.substring(3, 4).toLowerCase() + setterName.substring(4);

                Method getter = excepted.getClass().getMethod(getterName);

                assertEquals(getter.invoke(excepted), getter.invoke(actual));
            }
        }
    }
}
