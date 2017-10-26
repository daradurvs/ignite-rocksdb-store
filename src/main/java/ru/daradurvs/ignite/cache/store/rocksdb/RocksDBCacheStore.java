package ru.daradurvs.ignite.cache.store.rocksdb;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import ru.daradurvs.ignite.cache.store.rocksdb.serializer.JavaSerializer;
import ru.daradurvs.ignite.cache.store.rocksdb.serializer.Serialiazer;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class RocksDBCacheStore<K, V> extends CacheStoreAdapter<K, V> {
    private final RocksDB db;
    //    private final ColumnFamilyHandle handle;
    private final Serialiazer serializer;

    public RocksDBCacheStore(RocksDB db, ColumnFamilyHandle handle) {
        this.db = db;
//        this.handle = handle;
        this.serializer = new JavaSerializer();
    }

    @SuppressWarnings("unchecked")
    @Override public V load(K key) throws CacheLoaderException {
        try {
//            return (V)serializer.deserialize(db.get(handle, serializer.serialize(key)));
            return (V)serializer.deserialize(db.get(serializer.serialize(key)));
        }
        catch (RocksDBException e) {
            throw new CacheLoaderException("Couldn't load a value for the key: " + key, e);
        }
    }

    @Override public void write(Cache.Entry<? extends K, ? extends V> entry) throws CacheWriterException {
        try {
//            db.put(handle,
//                serializer.serialize(entry.getKey()),
//                serializer.serialize(entry.getValue())
//            );
            db.put(serializer.serialize(entry.getKey()),
                serializer.serialize(entry.getValue())
            );
        }
        catch (RocksDBException e) {
            throw new CacheWriterException("Couldn't put entry, key: " + entry.getKey() + "; value: " + entry.getValue(), e);
        }
    }

    @Override public void delete(Object key) throws CacheWriterException {
        try {
//            db.delete(handle, serializer.serialize(key));
            db.delete(serializer.serialize(key));
        }
        catch (RocksDBException e) {
            throw new CacheWriterException("Couldn't delete a value for the key: " + key, e);
        }
    }
}
