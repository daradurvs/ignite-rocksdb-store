package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.marshaller.Marshaller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Serializer implementation using default JVM tools.
 */
public class BinarySerializer implements Serialiazer {
    private Marshaller marshaller = null;

    public BinarySerializer(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    /** {@inheritDoc} */
    @Override public byte[] serialize(@Nullable Object obj) {
        if (obj == null)
            return null;

        try {
            return marshaller.marshal(obj);
        }
        catch (IgniteCheckedException e) {
            throw new IllegalArgumentException("Couldn't serialize object of the class: " + obj.getClass().getSimpleName(), e);
        }
    }

    /** {@inheritDoc} */
    @Override public Object deserialize(@NotNull byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return marshaller.unmarshal(bytes, null);
        }
        catch (IOException | IgniteCheckedException e) {
            throw new IllegalArgumentException("Couldn't deserialize bytes array: " + Arrays.toString(bytes), e);
        }
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return this.getClass().getSimpleName();
    }
}
