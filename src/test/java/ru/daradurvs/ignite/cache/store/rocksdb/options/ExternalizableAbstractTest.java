package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.DBOptions;
import org.rocksdb.DBOptionsInterface;
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
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objStream = new ObjectOutputStream(byteStream)) {

            obj.writeExternal(objStream);

            objStream.flush();

            byteStream.flush();

            return byteStream.toByteArray();
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

        for (Method method : excepted.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.getName().startsWith("set")) {
                String setterName = method.getName();

                if (isIgnored(excepted.getClass(), setterName))
                    continue;

                String getterName = setterName.substring(3, 4).toLowerCase() + setterName.substring(4);

                Method getter = excepted.getClass().getMethod(getterName);

                assertEquals(getter.invoke(excepted), getter.invoke(actual));
            }
        }
    }

    protected boolean isIgnored(@NotNull Class cls, String setterName) {
        if (DBOptions.class == cls || DBOptionsInterface.class.isAssignableFrom(cls)) {
            switch (setterName) {
                case "setLogger":
                case "setRateLimiter":
                case "setEnv":
                case "setIncreaseParallelism":
                    return true;
            }
        }

        return false;
    }
}
