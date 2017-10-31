package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Vyacheslav Daradur
 * @since 31.10.2017
 */
public class KryoSerializer implements Serialiazer {
    private static final int BUFFER_SIZE = 4096;

    private Kryo kryo = new Kryo();

    @Override public byte[] serialize(@Nullable Object obj) {
        if (obj == null)
            return null;

        try (Output out = new Output(BUFFER_SIZE)) {
            kryo.writeClassAndObject(out, obj);
            out.flush();
            return out.toBytes();
        }
    }

    @Override public Object deserialize(@NotNull byte[] bytes) {
        try (Input in = new Input(bytes)) {
            return kryo.readClassAndObject(in);
        }
    }

    @Override public String toString() {
        return this.getClass().getSimpleName();
    }
}
