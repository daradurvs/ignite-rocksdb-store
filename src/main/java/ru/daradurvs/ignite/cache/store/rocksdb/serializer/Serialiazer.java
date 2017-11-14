package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Serializer interface. Defines methods for serialization/deserialization object to bytes array and vice versa.
 */
public interface Serialiazer {
    /**
     * Serialize given object to bytes array.
     *
     * @param obj Object for serialization.
     * @return Bytes array.
     */
    public byte[] serialize(@Nullable Object obj);

    /**
     * Deserialize given bytes array.
     *
     * @param arr Bytes array.
     * @return Deserialized object.
     */
    public Object deserialize(@NotNull byte[] arr);
}
