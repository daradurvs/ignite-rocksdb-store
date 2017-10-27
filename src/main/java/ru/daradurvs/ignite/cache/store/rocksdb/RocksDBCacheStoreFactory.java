package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.Serializable;
import javax.cache.configuration.Factory;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class RocksDBCacheStoreFactory<K, V> implements Factory<RocksDBCacheStore<K, V>>, Serializable {
    private String cacheName;

    static {
        RocksDB.loadLibrary();
    }

    public RocksDBCacheStoreFactory(String cacheName) {
        this.cacheName = cacheName;
    }

    /** {@inheritDoc} */
    @Override public RocksDBCacheStore<K, V> create() {
        try {
            RocksDB db = DBManager.db();
            ColumnFamilyHandle handle = DBManager.initColumnFamilyHandle(cacheName);

            return new RocksDBCacheStore<>(db, handle);
        }
        catch (RocksDBException e) {
            throw new IllegalStateException(e);
        }
    }
}
