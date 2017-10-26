package ru.daradurvs.ignite.cache.store.rocksdb.serializer;

/**
 * @author Vyacheslav Daradur
 * @since 26.10.2017
 */
public interface Serialiazer {
    public byte[] serialize(Object obj);

    public Object deserialize(byte[] arr);
}
