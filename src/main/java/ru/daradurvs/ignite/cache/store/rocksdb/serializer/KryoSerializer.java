package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Serializer implementation using Kryo.
 */
public class KryoSerializer implements Serialiazer {
    private static final int BUFFER_SIZE = 4096;

    private Kryo kryo = new Kryo();

    /** {@inheritDoc} */
    @Override public byte[] serialize(@Nullable Object obj) {
        if (obj == null)
            return null;

        try (Output out = new Output(BUFFER_SIZE)) {
            kryo.writeClassAndObject(out, obj);
            out.flush();
            return out.toBytes();
        }
    }

    /** {@inheritDoc} */
    @Override public Object deserialize(@NotNull byte[] bytes) {
        try (Input in = new Input(bytes)) {
            return kryo.readClassAndObject(in);
        }
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return this.getClass().getSimpleName();
    }
}
