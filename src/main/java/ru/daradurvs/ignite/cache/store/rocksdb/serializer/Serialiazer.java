package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Vyacheslav Daradur
 * @since 26.10.2017
 */
public interface Serialiazer {
    public byte[] serialize(@Nullable Object obj);

    public Object deserialize(@NotNull byte[] arr);
}
